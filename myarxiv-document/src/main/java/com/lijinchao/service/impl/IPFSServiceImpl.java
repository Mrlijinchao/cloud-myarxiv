package com.lijinchao.service.impl;

import com.lijinchao.service.IPFSService;
import com.lijinchao.uitls.IPFSUtil;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class IPFSServiceImpl implements IPFSService {

    @Resource
    IPFSUtil ipfsUtil;

    @Override
    public String saveFile(InputStream fileInputStream) throws IOException {
        IPFS ipfs = ipfsUtil.getIPFSInstance();
        NamedStreamable.InputStreamWrapper inputStreamWrapper = new NamedStreamable.InputStreamWrapper(fileInputStream);
//      NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("D:\\Data\\图\\cover5.jpg"));
        MerkleNode response = ipfs.add(inputStreamWrapper).get(0);
        String cid = response.hash.toBase58();
        log.info("cid:"+cid);
        return cid;
    }

    @Override
    public byte[] getFile(String cid) throws IOException {
        IPFS ipfs = ipfsUtil.getIPFSInstance();
        Multihash multihash = Multihash.fromBase58(cid);
        byte[] content = ipfs.cat(multihash);
        return content;
    }

    @Override
    public void removeFile(String cid) throws IOException {
        IPFS ipfs = ipfsUtil.getIPFSInstance();
        Multihash filePointer = Multihash.fromBase58(cid);
        // 这个只是解除这个节点对文件的固定，需要过一段时间ipfs的GC才会清理文件，如果其他节点pin了这个文件，那么这个文件还是会继续存在
        ipfs.pin.rm(filePointer);
    }
}
