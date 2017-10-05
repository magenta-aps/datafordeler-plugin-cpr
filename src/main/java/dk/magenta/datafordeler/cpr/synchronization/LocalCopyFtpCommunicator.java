package dk.magenta.datafordeler.cpr.synchronization;

import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.HttpStatusException;
import dk.magenta.datafordeler.core.plugin.FtpCommunicator;
import dk.magenta.datafordeler.core.util.CloseDetectInputStream;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by jubk on 05-07-2017.
 */
public class LocalCopyFtpCommunicator extends FtpCommunicator {

  private Path localCopyFolder;

  public LocalCopyFtpCommunicator(String username, String password, boolean useFtps,
      String proxyString, String localCopyFolder) throws IOException {
    super(username, password, useFtps, proxyString);
    this.localCopyFolder = Paths.get(localCopyFolder);
    if (!Files.isDirectory(this.localCopyFolder)) {
      throw new IOException("Local copy folder for FTP download " +
          localCopyFolder + " is not a directory");
    }
  }

  @Override
  public InputStream fetch(URI uri) throws HttpStatusException, DataStreamException {
    System.out.println("step 1");
    System.out.println(uri);

    try {
      FTPClient ftpClient = this.performConnect(uri);

      System.out.println("step 2");
      List<String> remotePaths = Arrays.asList(ftpClient.listNames(uri.getPath()));
      remotePaths.sort(Comparator.naturalOrder());
      List<String> downloadPaths = this.filterFilesToDownload(remotePaths);
      System.out.println("step 3");

      System.out.println("Filenames on server "+uri+": "+downloadPaths);

      ArrayList<String> currentFiles = new ArrayList<>();
      for (String path : downloadPaths) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        Path outputFile = Files.createFile(Paths.get(localCopyFolder.toString(), fileName));
        OutputStream outputStream = Files.newOutputStream(outputFile);
        ftpClient.retrieveFile(path, outputStream);
        // ftpClient.completePendingCommand();
        outputStream.close();
        currentFiles.add(fileName);
      }
      System.out.println("step 4");
      ftpClient.disconnect();
      System.out.println("step 5");

      final List<String> finalList = new ArrayList<>(currentFiles);
      InputStream inputStream = this.buildChainedInputStream(finalList);
      System.out.println("step 6");

      if (inputStream != null) {
        CloseDetectInputStream inputCloser = new CloseDetectInputStream(inputStream);
        inputCloser.addAfterCloseListener(new Runnable() {
          @Override
          public void run() {
            try {
              markLocalFilesAsDone(finalList);
              ftpClient.disconnect();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
        inputStream = inputCloser;
      } else {
        inputStream = new ByteArrayInputStream("".getBytes());
      }

      return inputStream;

    } catch (IOException e) {
      System.out.println("whoa, fail!");
      e.printStackTrace();
      throw new DataStreamException(e);
    }
  }

  private void markLocalFilesAsDone(List<String> fileNames) throws IOException {
    for(String fileName : fileNames) {
      if(!fileName.endsWith(DONE_FILE_ENDING)) {
        String doneFileName = fileName + DONE_FILE_ENDING;
        Files.move(
            Paths.get(localCopyFolder.toString(), fileName),
            Paths.get(localCopyFolder.toString(), doneFileName)
        );
      }
    }
  }

  protected Set<String> getLocalFilenameList() throws IOException {
    Set<String> knownFiles = new HashSet<>();
    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(localCopyFolder);
    for(Path path : directoryStream) {
      if(Files.isRegularFile(path)) {
        knownFiles.add(path.getFileName().toString());
      }
    }
    return knownFiles;
  }

  protected InputStream buildChainedInputStream(List<String> fileNames) throws IOException {
    List<String> fileNamesToProcess = new ArrayList<>(fileNames);
    fileNamesToProcess.sort(Comparator.naturalOrder());

    InputStream inputStream = null;

    for(String fileName : fileNamesToProcess) {
      if(!fileName.endsWith(DONE_FILE_ENDING)) {
        Path filePath = Paths.get(localCopyFolder.toString(), fileName);
        InputStream newInputStream = Files.newInputStream(filePath);
        if(inputStream == null) {
          inputStream = newInputStream;
        } else {
          inputStream = new SequenceInputStream(inputStream, newInputStream);
        }
      }
    }

    return inputStream;
  }

  protected List<String> filterFilesToDownload(List<String> paths) throws IOException {
    Set<String> knownFiles = getLocalFilenameList();
    List<String> result = new ArrayList<>();
    for(String path : paths) {
      String fileName = path.substring(path.lastIndexOf('/') + 1);
      if(!knownFiles.contains(fileName) && !knownFiles.contains(fileName + DONE_FILE_ENDING)) {
        result.add(path);
      }
    }
    return result;
  }



}
