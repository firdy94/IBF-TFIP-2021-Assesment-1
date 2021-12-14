package com.ibftfip2021;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer {
	private int portNum;
	private List<String> inputPath = new ArrayList<>();

	public HTTPServer(int portNum, List<String> inputPath) {
		this.portNum = portNum;
		this.inputPath = inputPath;

	}

	public void startServer() throws IOException {
		ExecutorService threadPool = Executors.newFixedThreadPool(3);
		ServerSocket server = new ServerSocket(portNum);
		checkPaths(server);
		System.out.printf("Listening at port %d...", portNum);

		try {
			while (!server.isClosed()) {
				server.setSoTimeout(1200000);
				Socket socket = server.accept();
				HTTPClientConnection clientSocket = new HTTPClientConnection(socket, inputPath);
				threadPool.submit(clientSocket);
				// Thread nThread= new Thread(clientSocket);
				// nThread.start();
			}
		} catch (SocketTimeoutException s) {
			System.out.println("Server shutting down due to inactivity");
			threadPool.shutdown();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkPaths(ServerSocket server) {
		for (String path : inputPath) {
			File inputPathDir = Paths.get(path).toFile();

			if (!inputPathDir.exists()) {
				System.out.println("Input path does not exist");
				closeServer(server);
				System.exit(1);
			} else if (!inputPathDir.isDirectory()) {
				System.out.println("Input path does not specify a directory");
				closeServer(server);
				System.exit(1);
			} else if (!inputPathDir.canRead()) {
				System.out.println("Input path cannot be read by server");
				closeServer(server);
				System.exit(1);
			}
		}
	}

	public void closeServer(ServerSocket server) {
		try {
			if (server != null) {
				server.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
