package com.thkmon.bbwin.main;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.ptr.IntByReference;

public class BBShowWindowInfos {
	private static int index = 0;

	public static void main(String[] args) {
		try {
			System.out.println("[Information of current window handles]");

			BBShowWindowInfos instance = new BBShowWindowInfos();
			instance.printAllHwndInformations();

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

					// 숨겨져 있는 창은 제외하고 찾는다. 최소화 되어있는 창은 포함한다.
					// rectangle.left값이 -32000일 경우 최소화되어 있는 창이다.
					// if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hwnd) &&
					// rectangle.left > -32000)) {
					if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hwnd))) {
						return true;
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

					buff.append("번호 : ").append((++index));
					buff.append(", ").append("pid : ").append(pid);
					buff.append(", ").append("클래스네임 : ").append(clsName);
					buff.append(", ").append("텍스트 : ").append(wText);
					buff.append(", ").append("위치 : (").append(rectangle.left).append(",").append(rectangle.top)
							.append(")");
					buff.append("~(").append(rectangle.right).append(",").append(rectangle.bottom).append(")");

					System.out.println(buff.toString());
					return true;
				}
			}, null);

		} catch (Exception e) {
			throw e;
		}
	}
}