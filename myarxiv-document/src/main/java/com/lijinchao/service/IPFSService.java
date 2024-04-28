package com.lijinchao.service;

import java.io.IOException;
import java.io.InputStream;

public interface IPFSService {

    String saveFile(InputStream fileInputStream) throws IOException;

    byte[] getFile(String cid) throws IOException;

    void removeFile(String cid) throws IOException;

}
