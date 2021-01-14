package com.thkmon.bbwin.main;

import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.ptr.IntByReference;

public class BBShowWindowInfos {
	private static int index = 0;
	private static ArrayList<String> hwndInfoList = null;

	public static void main(String[] args) {
		try {
			System.out.println("[Information of current window handles]");
			
			hwndInfoList = new ArrayList<String>();
			
			BBShowWindowInfos instance = new BBShowWindowInfos();
			instance.printAllHwndInformations();
			
			int count = hwndInfoList.size();
			for (int i=0; i<count; i++) {
				System.out.print("(" + (i+1) + "/" + count + ") ");
				System.out.println(hwndInfoList.get(i));	
			}
			
			System.out.println("end");

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// wait for 10 minutes after run
			Thread.sleep(1000 * 60 * 10);
		} catch (Exception e) {
			// ignore
		}
	}

	private void printAllHwndInformations() throws Exception {
		try {
			User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
				public boolean callback(HWND hwnd, Pointer arg1) {
					char[] windowText = new char[512];
					User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
					String wText = Native.toString(windowText);

					RECT rectangle = new RECT();
					User32.INSTANCE.GetWindowRect(hwnd, rectangle);

					// 숨겨져 있는 창은 제외하고 찾기
					if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hwnd))) {
						return true;
					}
					
					// 최소화 여부
					boolean bMinimized = false;
					if (rectangle.left <= -32000) {
						bMinimized = true;
					}

					// 핸들(hwnd)의 클래스 네임 가져오기
					char[] c = new char[512];
					User32.INSTANCE.GetClassName(hwnd, c, 512);
					String clsName = String.valueOf(c).trim();

					// 핸들(hwnd)의 pid 가져오기
					IntByReference pidByRef = new IntByReference(0);
					User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidByRef);
					int pid = pidByRef.getValue();

					StringBuilder buff = new StringBuilder();
					buff.append("PID : ").append(pid);
					buff.append("\n").append("CLASSNAME : ").append(clsName);
					buff.append(" / ").append("TEXT : ").append(wText);
					buff.append("\n").append("MINIMIZED : ").append(bMinimized);
					buff.append(" / ").append("POSITION : (").append(rectangle.left).append(",").append(rectangle.top).append(")");
					buff.append("~(").append(rectangle.right).append(",").append(rectangle.bottom).append(")");
					buff.append("\n");
					
					hwndInfoList.add(buff.toString());
					
					return true;
				}
			}, null);

		} catch (Exception e) {
			throw e;
		}
	}
}