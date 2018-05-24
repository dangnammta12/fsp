/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author namdv
 */
public class ZipUtils {

    public static boolean zipFolder(File inputFolder, String pathFileZip) {
        boolean result = false;
        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(pathFileZip)) {
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
                    File[] contents = inputFolder.listFiles();
                    for (File f : contents) {
                        if (f.isFile()) {
                            zipFile(f, zipOutputStream);
                        }
                    }
                    result = true;
                    zipOutputStream.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean zipFolder(long folderId, String pathDirectory, String pathFileZip) {
        boolean result = false;
        try (FileOutputStream fileOutputStream = new FileOutputStream(pathFileZip)) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
                compressDirectory(folderId, pathDirectory, zipOutputStream);
                result = true;
                zipOutputStream.closeEntry();
            } catch (Exception e) {
                Logger.getLogger(ZipUtils.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (IOException ex) {
            Logger.getLogger(ZipUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static void compressDirectory(long folderId, String directory, ZipOutputStream out) throws IOException {
        File fileToCompress = new File(directory);
        // list contents.
        System.out.println("directory=" + directory);
        String[] arrPath = directory.split("FSP_STATIC/" + folderId+"/");
        String parentPath = "";
        if (arrPath != null && arrPath.length > 1) {
            parentPath = arrPath[1] + File.separator;
        }
        System.out.println("parentPath=" + parentPath);
        String[] contents = fileToCompress.list();
        // iterate through directory and compress files.
        for (String content : contents) {
            File f = new File(directory, content);
            // testing type. directories and files have to be treated separately.
            if (f.isDirectory()) {
                // add empty directory
                if (f.list().length == 0) {
                    out.putNextEntry(new ZipEntry(parentPath + f.getName() + File.separator));
                }
//                out.putNextEntry(new ZipEntry(f.getName() + File.separator));
                // initiate recursive call
                compressDirectory(folderId, f.getAbsolutePath(), out);
            } else {
                // create ZipEntry and add to outputting stream.
                // prepare stream to read file.
                try (FileInputStream in = new FileInputStream(f)) {
                    // create ZipEntry and add to outputting stream.
                    out.putNextEntry(new ZipEntry(parentPath + f.getName()));
                    // write the data.
                    int len;
                    byte[] data = new byte[1024];
                    while ((len = in.read(data)) > 0) {
                        out.write(data, 0, len);
                    }
                    out.flush();
                    out.closeEntry();
                }
            }
        }
    }

    private static void zipFile(File inputFile, ZipOutputStream zipOutputStream) {
        try {
            ZipEntry zipEntry = new ZipEntry(inputFile.getName());
            zipOutputStream.putNextEntry(zipEntry);
            try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buf)) > 0) {
                    zipOutputStream.write(buf, 0, bytesRead);
                }

            }
            zipOutputStream.closeEntry();
            System.out.println("Regular file :" + inputFile.getCanonicalPath() + " is zipped to archive folder");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public boolean unZipFolder(String zipFile, String outputFolder) {
        boolean result = false;
        byte[] buffer = new byte[1024];
        try {
            //create output directory is not exists
            try {
                File folder = new File(outputFolder);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                //get the zip file content
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                //get the zipped file list entry
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String fileName = ze.getName();
                    File newFile = new File(outputFolder + File.separator + fileName);
                    System.out.println("file unzip : " + newFile.getAbsoluteFile());
                    //create all non exists folders
                    //else you will hit FileNotFoundException for compressed folder
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    ze = zis.getNextEntry();
                }

                zis.closeEntry();
                zis.close();
                result = true;
                System.out.println("Done");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ZipUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
