package server;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String line;
        String httpCommand = null;
        String uri = null;
        String uriNew = null;
        String[] uriSegments = null;
        Map<String, String> parameters = new HashMap<>();
        byte[] content = null;
        int contentLength = 0;

        line = reader.readLine();
        if (line != null) {
            String[] requestLineParts = line.split(" ");
            if (requestLineParts.length >= 2) {
                httpCommand = requestLineParts[0];
                uri = requestLineParts[1];

                int queryIndex = uri.indexOf("?");
                if (queryIndex != -1) {
                    String queryString = uri.substring(queryIndex + 1);
                    uriNew = uri.substring(0, queryIndex);
                    String[] paramPairs = queryString.split("&");
                    for (String pair : paramPairs) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2) {
                            parameters.put(keyValue[0], keyValue[1]);
                        }
                    }
                } else {
                    uriNew = uri;
                }

                String[] rawSegments = uriNew.split("/");
                List<String> filteredSegments = new ArrayList<>();
                for (String segment : rawSegments) {
                    if (!segment.isEmpty()) {
                        filteredSegments.add(segment);
                    }
                }
                uriSegments = filteredSegments.toArray(new String[0]);
            }
        }

        boolean inContent = false;
        StringBuilder contentBuilder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                inContent = true;
                continue;
            }

            if (inContent) {
                contentBuilder.append(line).append("\n");
            } else {
                // Parse headers
                String[] headerParts = line.split(": ");
                if (headerParts.length == 2) {
                    if (headerParts[0].equalsIgnoreCase("Content-Length")) {
                        contentLength = Integer.parseInt(headerParts[1]);
                    } else if (headerParts[0].equalsIgnoreCase("filename")) {
                        parameters.put("filename", headerParts[1]);
                    }
                }
            }
        }


        if (contentLength > 0 && contentBuilder.length() > 0) {
            String contentString = contentBuilder.toString();
            int filenameIndex = contentString.indexOf("filename=\"");
            if (filenameIndex != -1) {
                int start = filenameIndex + "filename=\"".length();
                int end = contentString.indexOf("\"", start);
                if (end != -1) {
                    String filename = contentString.substring(start, end);
                    filename = "\"" + filename + "\"";
                    parameters.put("filename", filename);

                    int contentTypeIndex = contentString.indexOf("Content-Type:");
                    if (contentTypeIndex != -1) {
                        start = contentString.indexOf("\n", contentTypeIndex);
                        start = contentString.indexOf("\n", start);
                        String tmp = contentString.substring(start);
                        end = tmp.indexOf("----");
                        if (end != -1) {
                            tmp = tmp.substring(0, end).trim();
                            parameters.put("fileContent", tmp);
                            content = tmp.getBytes();
                        } else {
                            content = contentString.getBytes();
                        }
                    }
                    else{
                        int contentStartIndex = contentString.indexOf("\n", end) + 1;
                        contentStartIndex = contentString.indexOf("\n") + 1;
                        content = contentString.substring(contentStartIndex).getBytes();
                    }
                }
            } else {
                content = contentString.getBytes();
            }
        } else {
            content = contentBuilder.toString().getBytes();
        }

        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }

    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }


        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }

    }
}

