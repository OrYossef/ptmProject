package server;


import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.net.ServerSocket;

public class MyHTTPServer extends Thread implements HTTPServer {

    private ConcurrentHashMap<String, Servlet> getServlets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> postServlets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> deleteServlets = new ConcurrentHashMap<>();
    private ExecutorService requestHandlerPool;
    private ServerSocket serverSocket;
    private volatile boolean isServerStopped = false;
    private final int port;
    private final int threadCount;

    public MyHTTPServer(int port, int threadCount) {
        requestHandlerPool = Executors.newFixedThreadPool(threadCount);
        this.port = port;
        this.threadCount = threadCount;
    }

    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        if (uri == null || servlet == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getServlets.put(uri, servlet);
                break;
            case "POST":
                postServlets.put(uri, servlet);
                break;
            case "DELETE":
                deleteServlets.put(uri, servlet);
                break;
        }
    }

    public void removeServlet(String httpCommand, String uri) {
        if (uri == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getServlets.remove(uri);
                break;
            case "POST":
                postServlets.remove(uri);
                break;
            case "DELETE":
                deleteServlets.remove(uri);
                break;
        }
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            serverSocket.setSoTimeout(1000);

            while (!isServerStopped) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    requestHandlerPool.submit(() -> {
                        try {
                            Thread.sleep(125);
                            BufferedReader requestReader = createBufferedReader(clientSocket);

                            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(requestReader);
                            ConcurrentHashMap<String, Servlet> servletMap;

                            if (requestInfo != null) {
                                switch (requestInfo.getHttpCommand()) {
                                    case "GET":
                                        servletMap = getServlets;
                                        break;
                                    case "POST":
                                        servletMap = postServlets;
                                        break;
                                    case "DELETE":
                                        servletMap = deleteServlets;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP command: " + requestInfo.getHttpCommand());
                                }

                                String bestMatchUri = "";
                                Servlet matchingServlet = null;
                                for (Map.Entry<String, Servlet> entry : servletMap.entrySet()) {
                                    if (requestInfo.getUri().startsWith(entry.getKey()) && entry.getKey().length() > bestMatchUri.length()) {
                                        bestMatchUri = entry.getKey();
                                        matchingServlet = entry.getValue();
                                    }
                                }

                                if (matchingServlet != null) {
                                    matchingServlet.handle(requestInfo, clientSocket.getOutputStream());
                                }
                            }
                            requestReader.close();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    if (isServerStopped) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader createBufferedReader(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        int availableBytes = inputStream.available();
        byte[] buffer = new byte[availableBytes];
        int bytesRead = inputStream.read(buffer, 0, availableBytes);

        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(buffer, 0, bytesRead)
                )
        );
    }

    public void start() {
        isServerStopped = false;
        super.start();
    }

    public void close() {
        isServerStopped = true;
        requestHandlerPool.shutdownNow();
    }

}

