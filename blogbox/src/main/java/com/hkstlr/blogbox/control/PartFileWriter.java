package com.hkstlr.blogbox.control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.mail.MessagingException;
import javax.mail.Part;


public class PartFileWriter {

    String messageId = StringPool.STRING;
    String fileName;
    String fileLocation = "/etc/opt/blogbox/assets/imgs/";
    Part part;

    private Logger LOG = Logger.getLogger(this.getClass().getName());


    public PartFileWriter(){
        super();
    }

    public PartFileWriter(Part p){
        super();
        this.part = p;
        this.fileName = this.getFileName(part);
    }

    public PartFileWriter(String messageId, Part p){
        super();
        this.messageId = messageId;
        this.part = p;
        this.fileName = this.getFileName(part);
    }
    public String getFileName(){
        return this.fileName;
    }

    String getFileName(Part p) {
        String createFileName = StringPool.STRING;
        if(this.messageId.isBlank()){
            createFileName = Integer.toString(Math.abs(ThreadLocalRandom.current().nextInt()));
        } else {
            createFileName =  this.messageId;
        }
        
        createFileName = createFileName.concat(StringPool.DASH);
        try {
            if (!p.getFileName().isBlank()) {
                createFileName = createFileName.concat(p.getFileName());
            }
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE,"getFileName",e);
        }
        createFileName = PartFileWriter.fileNameSafeString(createFileName);
        return createFileName.toLowerCase();
    }
   
    @Asynchronous
    void createFile() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(this.part.getSize())) {
            File writeMe = new File(getFullFilePath());
            boolean isCreated = writeMe.createNewFile();
            if(isCreated){
                this.part.getDataHandler().writeTo(baos);
                this.writeFile(baos.toByteArray());
            }
            baos.close();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    String getFullFilePath(){
        return this.fileLocation.concat(this.fileName);
    }

    public void writeFile(byte[] bytes) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(getFullFilePath())) {
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, this.getClass().getName(), e);
			throw new FileNotFoundException(e.getMessage());
		} catch (NullPointerException ex) {
			LOG.log(Level.SEVERE, this.getClass().getName(), ex);
			throw new NullPointerException(ex.getMessage());
		}

	}

    @Asynchronous
    void createImageFileFromBase64String(String b64) throws IOException {
        this.createImageFileFromBase64String(this.fileLocation, this.fileName, b64);
    }

    @Asynchronous
    void createImageFileFromBase64String(String location, String fileName, String b64) throws IOException {
        File myObj = new File(location.concat(fileName));
        myObj.createNewFile();
        this.writeFile(Base64.getDecoder().decode(b64.getBytes()));
    }

    
    String getBase64String(Part p) {
        String b64 = StringPool.STRING;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(p.getSize())) {
            p.getDataHandler().writeTo(baos);
            b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            baos.close();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return b64;
    }

    public static String fileNameSafeString(String fileName) {
		String r = fileName;
		String filenameRegex = "[\\/:*?<>|+=]+";
		r =  r.replaceAll(filenameRegex, StringPool.STRING);
        r = r.replaceAll(StringPool.SPACE, StringPool.UNDERSCORE);
        r = r.replaceAll(StringPool.AT, StringPool.UNDERSCORE);
		return r;
	}
    
}
