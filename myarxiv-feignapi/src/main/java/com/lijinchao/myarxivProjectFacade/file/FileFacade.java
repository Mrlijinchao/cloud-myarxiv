package com.lijinchao.myarxivProjectFacade.file;

import com.lijinchao.entity.File;
import org.springframework.web.bind.annotation.*;

/**
 * 其他模块操作project模块file表
 */
public interface FileFacade {

    /**
     * 根据Id获取文件信息
     * @param id
     * @return
     */
    @GetMapping("")
    File getFileInfo(@RequestParam("id") Long id);

    /**
     * 保存文件信息
     * @param file
     */
    @PostMapping("")
    void saveFileInfo(@RequestBody File file);

    /**
     * 更新文件信息
     * @param file
     */
    @PutMapping("")
    void updateFileInfo(@RequestBody File file);

    /**
     * 根据md5检查文件是否已经存在于文件系统中，如果存在就不用再次存储了
     * @param md5
     * @return
     */
    @GetMapping("/byMD5")
    File getFileByMD5(@RequestParam String md5);

    /**
     * 删除文件信息
     * @param id
     */
    @DeleteMapping("")
    String removeFileInfo(@RequestParam Long id);

    @GetMapping("/getFileName")
    File getFileInfoByCid(@RequestParam String cid);

}
