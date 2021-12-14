package com.ibftfip2021;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTTPClientConnection implements Runnable {
	private final Socket socket;
	private List<String> inputPath = new ArrayList<>();
	private DataInputStream dataInS;
	private DataOutputStream dataOutS;
	private BufferedReader buffRead;
	private BufferedWriter buffWrite;

	String serverIn = "";

	public HTTPClientConnection(Socket socket, List<String> inputPath) {
		this.socket = socket;
		this.inputPath = inputPath;
	}

	@Override
	public void run() {
		try (InputStream is = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
			dataInS = new DataInputStream(new BufferedInputStream(is));
			dataOutS = new DataOutputStream(new BufferedOutputStream(out));
			Reader reader = new InputStreamReader(dataInS);
			buffRead = new BufferedReader(reader);
			Writer writer = new OutputStreamWriter(dataOutS);
			buffWrite = new BufferedWriter(writer);
			serverIn = buffRead.readLine();
			System.out.println(serverIn);
			clientInputParser();
		}

		catch (IOException e) {
			e.printStackTrace();

		} finally {
			closeSocket(socket);
		}
	}

	public void clientInputParser() throws IOException {
		List<String> inputSegments = new ArrayList<>();
		while (!socket.isClosed()) {
			for (String s : serverIn.split(" ")) {
				inputSegments.add(s);
			}
			if (inputSegments.get(1).equals("/")) {
				inputSegments.remove("/");
				inputSegments.add(1, "/index.html");
			}
			if (!inputSegments.get(0).equals("GET")) {
				String msg = String.format("HTTP/1.1 405 Method Not Allowed\r\n\r\n%s not supported\r\n",
						inputSegments.get(0));
				buffWrite.write(msg);
				buffWrite.flush();
				closeSocket(socket);
			}
			if (inputSegments.get(1).endsWith(".png")) {
				for (String dirPath : inputPath) {
					File filePath = Paths.get(dirPath, inputSegments.get(1)).toFile();
					if (!filePath.exists()) {
						String msg = String.format("HTTP/1.1 404 Not Found\r\n\r\n%s not supported\r\n",
								inputSegments.get(1));
						buffWrite.write(msg);
						buffWrite.flush();
						closeSocket(socket);
					} else {
						String msg = "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n";
						buffWrite.write(msg);
						buffWrite.flush();
						byte[] imgBytes = Files.readAllBytes(filePath.toPath());
						dataOutS.write(imgBytes, 0, imgBytes.length);
						dataOutS.flush();
						closeSocket(socket);
					}
				}
			} else {
				for (String dirPath : inputPath) {
					File filePath = Paths.get(dirPath, inputSegments.get(1)).toFile();
					if (!filePath.exists()) {
						String msg = String.format("HTTP/1.1 404 Not Found\r\n\r\n%s not supported\r\n",
								inputSegments.get(1));
						buffWrite.write(msg);
						buffWrite.flush();
						closeSocket(socket);

					} else {
						String msg = "HTTP/1.1 200 OK\r\n\r\n";
						buffWrite.write(msg);
						buffWrite.flush();
						byte[] imgBytes = Files.readAllBytes(filePath.toPath());
						dataOutS.write(imgBytes, 0, imgBytes.length);
						dataOutS.flush();
						closeSocket(socket);
					}

				}

			}
		}

	}

	public void closeSocket(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
