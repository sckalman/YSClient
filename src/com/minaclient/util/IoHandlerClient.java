/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.minaclient.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.yinshuo.utils.AESHelper;
import com.yinshuo.utils.AESUtils;
import com.yinshuo.utils.BulkEnum;
import com.yinshuo.utils.BulkEnum.TransitionEffect;
import com.yinshuo.utils.AES;
import com.yinshuo.utils.DES;
import com.yinshuo.utils.FileHelper;
import com.yinshuo.utils.FileUploadRequest;
/**
 * Sends its memory usage to the MemoryMonitor server.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class IoHandlerClient extends IoHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(IoHandlerClient.class);
    
    private static final String DEBUG_TAG = "MemMonClient";

    private IoSession session;

    private IoConnector connector;

    /**
     * Default constructor.
     */
    public IoHandlerClient() {

    	connector = new NioSocketConnector();  
     // 添加filter，codec为序列化方式。这里为对象序列化方式，即表示传递的是对象。
        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        
        LOGGER.debug("UDPClient::UDPClient");
        LOGGER.debug("Created a datagram connector");
        LOGGER.debug("Setting the handler");   
        LOGGER.debug("About to connect to the server...");  
        connector.setHandler(this);
        ConnectFuture connFuture;
		try {
			connFuture = connector.connect(new InetSocketAddress(
				InetAddress.getByName("127.0.0.1"), 10087));
		        connFuture.awaitUninterruptibly();
		        connFuture.addListener(new IoFutureListener<ConnectFuture>() {
		        	
		            public void operationComplete(ConnectFuture future) {
		                if (future.isConnected()) {
		                	System.out.println("...connected.");
		                    session = future.getSession();
		                    try {
		                        sendData(0);
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    } catch (IOException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                } else {
		                	System.out.println("Not connected...exiting");
		                }
		            }
		        });
			
		} catch (UnknownHostException e) {
			
			System.out.println(e.toString()+"errot");
			e.printStackTrace();
		}

    }
    
    public void sendData(int ret) throws InterruptedException, IOException, JSONException {   
    	JSONObject mJsonObject = new JSONObject();
        switch (ret) {
        
        case BulkEnum.ADV_STATUS.ADV_DEFAULT:
        	mJsonObject.put("cmd", ret); ;  
        	mJsonObject.put("TransitionEffect", TransitionEffect.CubeOut.ordinal());
	        session.write(mJsonObject.toString());

			break;
		case BulkEnum.ADV_STATUS.ADV_START:	
			mJsonObject.put("cmd", ret); ;  
			JSONArray jsonImgArray = new JSONArray();
			jsonImgArray.put( new JSONObject().put("path", BulkEnum.ImagePath+"001.jpg").put("name", "test0"));
			jsonImgArray.put( new JSONObject().put("path", BulkEnum.ImagePath+"002.jpg").put("name", "test1"));
			jsonImgArray.put( new JSONObject().put("path", BulkEnum.ImagePath+"003.jpg").put("name", "test2"));
			jsonImgArray.put( new JSONObject().put("path", BulkEnum.ImagePath+"004.jpg").put("name", "test3"));
			
/*			jsonImgArray.put(0, BulkEnum.ImagePath+"001.jpg");
			jsonImgArray.put(1, BulkEnum.ImagePath+"002.jpg");
			jsonImgArray.put(2, BulkEnum.ImagePath+"003.jpg");
			jsonImgArray.put(3, BulkEnum.ImagePath+"004.jpg");
			jsonImgArray.put(4, BulkEnum.ImagePath+"005.jpg");*/
			mJsonObject.put("jsonImgArray", jsonImgArray);
			mJsonObject.put("TransitionEffect", TransitionEffect.CubeOut.ordinal());
			
			System.out.println(mJsonObject.toString());
			session.write(mJsonObject.toString());
	        
			break;
		case BulkEnum.ADV_STATUS.ADV_STOP:
			mJsonObject.put("cmd", ret); ;  
	        session.write(mJsonObject.toString());
			break;
			
		case BulkEnum.ADV_STATUS.ADV_PIC_ADD:	
			Runtime.getRuntime().exec("/home/sc/software/android-studio/sdk/platform-tools/adb shell mkdir /data/data/yinshuo");
			Thread.sleep(500);
			Runtime.getRuntime().exec("/home/sc/software/android-studio/sdk/platform-tools/adb push /home/sc/pic/ /data/data/yinshuo");
			Thread.sleep(500);
			System.out.println("添加图片文件夹成功"); 
			break;
		case BulkEnum.ADV_STATUS.ADV_PIC_DELETE:	
			Runtime.getRuntime().exec("/home/sc/software/android-studio/sdk/platform-tools/adb shell rm -r /data/data/yinshuo/");
			Thread.sleep(500);
			System.out.println("删除图片文件夹成功");
			break;
		case BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN:// 打开键盘		
			mJsonObject.put("cmd", ret);
			System.out.println(mJsonObject.toString()+"--------key");
			//mJsonObject.setCmd(ret);
	        session.write(mJsonObject.toString());
			
	        break;
		case BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE: // 关闭键盘
			mJsonObject.put("cmd", ret); ;  
	        session.write(mJsonObject.toString());

			break;
		case BulkEnum.KEYBOARD_STATUS.KEYBOARD_SET_TIMEOUT: // 设置键盘超时
			mJsonObject.put("cmd", ret);
			mJsonObject.put("time", 60);
			session.write(mJsonObject.toString());
			break;

		case BulkEnum.DEVICE_STATUS.DEVICE_CONNECT: // 连接设备

			break;
		case BulkEnum.DEVICE_STATUS.DEVICE_IS_CONNECT: // 判断连接状态 返回 0
														// 连接上 返回-1 连接失败
														// 返回 -2 异常

			break;
		case BulkEnum.DEVICE_STATUS.DEVICE_STATUS_REBOOT:// 重启设备
			Runtime.getRuntime().exec("adb shell reboot");
			Thread.sleep(1000);

			break;
		case BulkEnum.DEVICE_STATUS.DEVICE_GET_ID:// 得到设备的ID
		

			break;
		case BulkEnum.DEVICE_STATUS.DEVICE_OPEN_APP:// 打开应用程序

			Runtime.getRuntime().exec("adb shell am broadcast -a NotifyAppStart");
			Thread.sleep(1000);
			break;
		case BulkEnum.DEVICE_STATUS.DEVICE_CLOSE_APP:// close app
			mJsonObject.put("cmd", ret);
			session.write(mJsonObject.toString());
			break;
		case BulkEnum.SIGN_STATUS.SIGN_OPEN:

			mJsonObject.put("cmd", ret);		
			System.out.println(mJsonObject.toString()+"--------SIGN_OPEN");
			session.write(mJsonObject.toString());
			break;
		case BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN:

			mJsonObject.put("cmd", ret);
			System.out.println(mJsonObject.toString());
			session.write(mJsonObject.toString());
			break;
		case BulkEnum.OTHER_MODULE.TTS_TRANSFER:

			mJsonObject.put("cmd", ret);
			mJsonObject.put("content", "您好，请输入密码");
			session.write(mJsonObject.toString());
			break;

		case BulkEnum.OTHER_MODULE.NEW_DIALOG:

			mJsonObject.put("cmd", ret);
			mJsonObject.put("content", "您好，您的取款金额为1000 元");

			JSONArray jsonArray = new JSONArray();
			jsonArray.put(0, "确定");
			jsonArray.put(1, "取消");
			mJsonObject.put("btn", jsonArray);
			System.out.println(mJsonObject.toString());
			session.write(mJsonObject.toString());
			break;
	
			
		case BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_CLOSE:  // 关闭屏幕
			mJsonObject.put("cmd", ret);
			System.out.println(mJsonObject.toString());
			session.write(mJsonObject.toString());
			
			
			break;
		case BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_OPEN:  // 打开屏幕


			mJsonObject.put("cmd", ret);
			System.out.println(mJsonObject.toString());
			session.write(mJsonObject.toString());
			
			break;	
			
			
			
		default:
			mJsonObject.put("cmd", ret);
			session.write(mJsonObject.toString());
			break;
		}
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
    	System.out.println("exceptionCaught : "+cause.getMessage());
        
    }
    JSONObject jsonObject;
    private BufferedOutputStream out;
    private int count;
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        System.out.println("Session recv : "+message.toString());
   
    	try {
			if (message instanceof FileUploadRequest) {
				// FileUploadRequest 为传递过程中使用的DO。
				FileUploadRequest request = (FileUploadRequest) message;
				System.out.println(request.getFilename());
				if (out == null) {
					// 新建一个文件输入对象BufferedOutputStream，随便定义新文件的位置
					out = new BufferedOutputStream(new FileOutputStream(
							request.getFilename()));
					out.write(request.getFileContent());
				} else {
					out.write(request.getFileContent());
				}
				count += request.getFileContent().length;

			} else if (message instanceof String) {
				
					jsonObject = new JSONObject(message.toString());
			        int cmd = jsonObject.getInt("cmd");

					switch (cmd) {
						case BulkEnum.KEYBOARD_STATUS.KEYBOARD_RECV_CMD:
							
						System.out.println("键盘内容"+jsonObject.getString("content")); 
						System.out.println("键盘解密内容"+DES.decryptDES(jsonObject.getString("content"), "87654321"));
					
						//System.out.println("键盘解密内容"+AESUtils.decrypt("a", jsonObject.getString("content")));

						//AESUtils.decrypt(jsonObject.getString("content"), "a");
						
						break;
							
						case BulkEnum.KEYBOARD_STATUS.KEYBOARD_SET_TIMEOUT:
							System.out.println("超时时间"+jsonObject.getLong("timeInterval"));
							
							break;
						case BulkEnum.SIGN_STATUS.SIGN_RECV_CMD_FINISH:
							
							System.out.println("签名图片传输完成");
							out.flush(); //// 这里是进行文件传输后，要进行flush和close否则传递的文件不完整。
							out.close();
							out = null;
							break;										
						case BulkEnum.EVALUTE_STATUS.EVALUTE_RECV_CMD:
							
							System.out.println("评价分数"+jsonObject.getDouble("content"));
							break;
					}
			}

		} catch (Exception e) {
			
			System.out.println(e.toString()+"errot");
			e.printStackTrace();
		}

    }

    @Override
    public void messageSent(IoSession session, Object message){
        System.out.println("Session sent : ");
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("Session closed : ");
    }

    @Override
    public void sessionCreated(IoSession session) {
        System.out.println("sessionCreated : ");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        System.out.println("sessionIdle : ");
    }

    @Override
    public void sessionOpened(IoSession session){
        System.out.println("sessionOpened : ");
    }

}
