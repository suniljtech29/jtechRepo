for (String instanceName : instanceNames) {
    for (ChannelSftp.LsEntry file : files) {
        if (file.getAttrs().isDir() && file.getFilename().matches(instanceName)) {
            continue; // Skip directories
        }

        String remoteFilePath = rootLogPath + "/" + file.getFilename();
        System.out.println("Attempting to copy file: " + remoteFilePath + " to " + localDestinationPath);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Download file content to outputStream
            sftpChannel.get(remoteFilePath, outputStream);

            // Check if the file content contains the fileProcessId
            if (outputStream.toString().contains(String.valueOf(fileProcessId))) {
                matchingFiles.add(file.getFilename());

                // Copy the file to local destination
                sftpChannel.get(remoteFilePath, localDestinationPath);
                System.out.println("Copied: " + file.getFilename() + " to " + localDestinationPath);
            }
        } catch (SftpException e) {
            System.err.println("Error copying file: " + file.getFilename() + " " + e.getMessage());
            e.printStackTrace();
        }
    }
}
