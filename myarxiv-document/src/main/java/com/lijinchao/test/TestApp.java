package com.lijinchao.test;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestApp {
    public static void main(String[] args) {
        getFile();
//        saveFile();
//        remove();
    }

    public static void saveFile(){
        IPFS ipfs = new IPFS("/ip4/192.168.153.145/tcp/5001");
        try {
            NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("D:\\Data\\cloud-myarxiv\\project\\myarxiv-document\\src\\main\\resources\\bootstrap.yml"));
            MerkleNode response = ipfs.add(file).get(0);
            System.out.println("Hash (base 58): " + response.hash.toBase58());
        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }
    }

    public static void getFile(){
        IPFS ipfs = new IPFS("/ip4/192.168.153.145/tcp/5001");
        try {
//            QmT3khT12q5jgbF6xmTU1RiTtD13dNRRVHavziVaMvtdPg
            String hash = "QmT3khT12q5jgbF6xmTU1RiTtD13dNRRVHavziVaMvtdPg"; // Hash of a file
            Multihash multihash = Multihash.fromBase58(hash);
            byte[] content = ipfs.cat(multihash);

            System.out.println("Content of " + hash + ": " + new String(content));
        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }
    }

    public static void remove() {
        IPFS ipfs = new IPFS("/ip4/192.168.153.145/tcp/5001");
        Multihash filePointer = Multihash.fromBase58("QmQP7VRBAfpLgdqPoaQ81mYDVujG7VTV73EKPmo6BE5Tf4");
        List<Multihash> rm = null;
        try {
            rm = ipfs.pin.rm(filePointer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(rm);
    }

}
