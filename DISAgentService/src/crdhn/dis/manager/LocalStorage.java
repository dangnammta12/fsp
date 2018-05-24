/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.manager;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.FileInfo;
import crdhn.dis.upload.UploadChunkWorker;
import crdhn.dis.utils.Utils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Part;

/**
 *
 * @author namdv
 */
public class LocalStorage {

    private final static String className = "LocalStorage";

    public static void writeFileUploadBrowser(FileInfo fInfo, Part filePart) {
        try {
            String pathFileTmp = Configuration.path_folder_store + "/" + fInfo.fileName;
            if (Utils.checkFileExisted(pathFileTmp)) {
                pathFileTmp += "_" + System.nanoTime();
            }
            fInfo.filePath = pathFileTmp;
            FileOutputStream fout;
            long fileSize;
            String hashSHA1;
            String hashMD5;
            try (InputStream filecontent = filePart.getInputStream()) {
                fout = new FileOutputStream(pathFileTmp);
                int len = -1;
                byte[] buffer = new byte[8192];
                filePart.delete();
                fileSize = 0;
                MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                MessageDigest digest_md5 = MessageDigest.getInstance("MD5");
                hashSHA1 = "";
                hashMD5 = "";
                while ((len = filecontent.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                    fileSize += len;
                    hashSHA1 = Utils.toHex(buffer, digest256);
                    hashMD5 = Utils.toHex(buffer, digest_md5);
                }
                buffer = null;
            }
            fout.close();
            fInfo.fileSize = fileSize;
            fInfo.checksumMD5 = hashMD5;
            fInfo.checksumSHA2 = hashSHA1;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writeFileUploadPath(FileInfo fInfo, String pathFile) {
        try {
//            String pathFileTmp = Configuration.path_folder_store + "/" + fInfo.fileName;
//            fInfo.filePath = pathFileTmp;
            fInfo.filePath = pathFile;
            fInfo.startTime = System.nanoTime();
            long fileSize;
            String hashSHA1;
            String hashMD5;
            try (BufferedInputStream f = new BufferedInputStream(
                    new FileInputStream(pathFile))) {
                fileSize = 0;
                MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                MessageDigest digest_md5 = MessageDigest.getInstance("MD5");
                hashSHA1 = "";
                hashMD5 = "";
//                try (FileOutputStream fout = new FileOutputStream(pathFileTmp)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = f.read(buffer)) != -1) {
//                        fout.write(buffer, 0, len);
                    fileSize += len;
                    hashSHA1 = Utils.toHex(buffer, digest256);
                    hashMD5 = Utils.toHex(buffer, digest_md5);
                }
//                }
            }
            fInfo.fileSize = fileSize;
            fInfo.checksumSHA2 = hashSHA1;
            fInfo.checksumMD5 = hashMD5;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean checkSHA(String sha_server, String pathFile) {
        try {
            String hashSHA1;
            try (BufferedInputStream f = new BufferedInputStream(
                    new FileInputStream(pathFile))) {
                MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                hashSHA1 = "";
                byte[] buffer = new byte[8192];
                int len;
                while ((len = f.read(buffer)) != -1) {
                    hashSHA1 = Utils.toHex(buffer, digest256);
                }
                if (sha_server != null && sha_server.equals(hashSHA1)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeFile(String path, long offset, byte[] data) {
        RandomAccessFile fout = null;
        try {
//            System.out.println("offset=" + offset + "\t data.length=" + data.length);
            fout = new RandomAccessFile(path, "rw");
            fout.seek(offset);
            fout.write(data, 0, data.length);
            fout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public static void writeFileInfo(String path, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String sourcePath, String destPath) {
        try {
            File source = new File(sourcePath);
            File dest = new File(destPath);
            FileOutputStream os;
            try (FileInputStream is = new FileInputStream(source)) {
                os = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
            os.close();
        } catch (Exception ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static FileInfo getFileInfoLocal(String path) {
        FileInfo fInfo = new FileInfo();
        String content = "";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                content += line;
            }
            fInfo.assignFromLocal(content);
        } catch (IOException e) {
            e.printStackTrace();
            fInfo = null;
        }

        return fInfo;
    }

    public static String getFileContent(String pathFile, long offset, int bufferSize) {
        try {
            byte[] arrData;
            try (RandomAccessFile raf = new RandomAccessFile(pathFile, "r")) {
                raf.seek(offset);
                arrData = new byte[bufferSize];
                raf.read(arrData, 0, bufferSize);
            }
            return Base64.getUrlEncoder().encodeToString(arrData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Path rootPath = Paths.get(Configuration.path_folder_store);
        try {
            long size = Files.walk(rootPath)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
            System.out.println("size=" + size);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static synchronized void checkLimitLocalStorage(long fileSize, String shaCurrentFile) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Path rootPath = Paths.get(Configuration.path_folder_store);
            try {
                long size = Files.walk(rootPath)
                        .filter(p -> p.toFile().isFile())
                        .mapToLong(p -> p.toFile().length())
                        .sum();
                if (fileSize + size >= Configuration.limit_local_store_max) {
                    Object[] listFile = Files.walk(rootPath).filter(p -> p.toFile().isFile()).sorted().map(Path::toFile).toArray();
                    int index = 0;
                    List<String> listFileUploading = FileManager.getListSHAFileUploading();
                    while (fileSize + size >= Configuration.limit_local_store_min && index < listFile.length / 2) {
                        boolean isCurrentFile = false;
                        for (int i = index; i <= listFile.length / 2; i++) {
                            try {
                                index = i;
                                File file = (File) listFile[i];
                                String filename = file.getName();
                                if (filename.contains(shaCurrentFile) || listFileUploading.contains(filename)) {
                                    isCurrentFile = true;
                                    continue;
                                }
                                String parentPath = file.getParent();
                                String newFileName;
                                if (filename.contains(".info")) {
                                    newFileName = filename.replaceFirst(".info", ".content");
                                } else {
                                    newFileName = filename.replaceFirst(".content", ".info");
                                }
                                String pathNewFile = parentPath + File.separator + newFileName;
                                file.delete();
                                File fOther = new File(pathNewFile);
                                if (fOther.exists()) {
                                    fOther.delete();
                                }
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (index >= listFile.length / 2 - 1 || isCurrentFile) {
                            break;
                        }
                        size = Files.walk(rootPath)
                                .filter(p -> p.toFile().isFile())
                                .mapToLong(p -> p.toFile().length())
                                .sum();
                    }
                }

            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                executor.shutdown();
            }
        });

    }

    public static List<Long> getFileInfosLocal() {
        Path rootPath = Paths.get(Configuration.path_folder_store);
        List<Long> fileIds = new ArrayList();
        try {
            Object[] arrFileName = Files.list(rootPath).filter(path -> path.toString().endsWith(".info")).sorted().toArray();
            for (Object name : arrFileName) {
                try {
                    String fileName = name.toString();
                    if (fileName != null) {
                        FileInfo fInfo = getFileInfoLocal(fileName);
                        if (fInfo != null && fInfo.fileId > 0 && fInfo.fileStatus == 1) {
                            fileIds.add(fInfo.fileId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileIds;
    }
}
