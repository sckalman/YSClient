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

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import org.json.JSONException;

import com.yinshuo.utils.AES;
import com.yinshuo.utils.AESHelper;
import com.yinshuo.utils.AESUtils;


/**
 * The class that will accept and process clients in order to properly
 * track the memory usage.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class MemoryMonitor {


    protected void recvUpdate(SocketAddress clientAddr, String update) {
    }




    public static void main(String[] args) throws IOException {
    	
        try {
        /*	Runtime.getRuntime().exec("adb shell mkdir /data/data/yinshuo");
			Thread.sleep(500);
			Runtime.getRuntime().exec("adb push /pic/ /data/data/yinshuo");
			Thread.sleep(500);*/
			Runtime.getRuntime().exec("/home/sc/software/android-studio/sdk/platform-tools/adb shell am broadcast -a NotifyServiceStop");
			Thread.sleep(500);		
			Runtime.getRuntime().exec("/home/sc/software/android-studio/sdk/platform-tools/adb shell am broadcast -a NotifyServiceStart");
			Thread.sleep(2000);
			Runtime.getRuntime().exec("/home/sc/software/android-studio/sdk/platform-tools/adb forward tcp:10087 tcp:10087"); // 端口转换
			Thread.sleep(500);
			
			//AESHelper.encrypt("adddd", "ddddddddddddddddddddddddd");
			//AES.decrypt("a", "B30A4E00EF140D2247408484A3EF29B5");
			
			AESUtils.encrypt("a", "1111111111");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    	IoHandlerClient  client = new IoHandlerClient();	
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(
				System.in));
    	System.out.print("请输入协议规定的数字进行操作,退出输入exit：");
    	while(true){
    		String strWord = br.readLine();// 从控制台输入
    		int ret = Integer.parseInt(strWord);
    		try {
				client.sendData(ret);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
		
    }
}
