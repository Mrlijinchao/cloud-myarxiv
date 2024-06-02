package com.lijinchao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Privilege;
import com.lijinchao.service.PrivilegeService;
import com.lijinchao.utils.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/privilege")
public class PrivilegeController {

    @Resource
    PrivilegeService privilegeService;

    /**
     * 新增权限
     * @param privilege
     * @return
     */
    @PostMapping("")
    public BaseApiResult addPrivilege(@RequestBody Privilege privilege) {
        try {
            privilegeService.savePrivilege(privilege);
        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success("新增权限成功！");
    }

    /**
     * 修改权限
     * @param privilege
     */
    @PutMapping("")
    public BaseApiResult updatePrivilege(@RequestBody Privilege privilege) {
        try {
            privilegeService.updatePrivilege(privilege);
        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.MODIFY_FAILED);
        }
        return BaseApiResult.success(MessageConstant.MODIFY_SUCCESS);
    }

    /**
     * 权限删除
     * @param ids
     * @return
     */
    @DeleteMapping("")
    public BaseApiResult deletePrivileges(@RequestBody List<Long> ids) {
        try {
            privilegeService.batchDelete(ids);
        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.DELETE_FAILED);
        }
        return BaseApiResult.success(MessageConstant.DELETE_SUCCESS);
    }

    /**
     * 分页查询权限
     * @param privilege
     * @return
     */
    @GetMapping("/query")
    public BaseApiResult queryPrivilege(Privilege privilege){
        try {
            Page<Privilege> page = privilegeService.queryPagePrivilege(privilege);
            return BaseApiResult.success(page);
        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.DELETE_FAILED);
        }
    }

    /**
     * 多条件分页查询
     * @param privilegeName
     * @param privilegeType
     * @param privilegeCode
     * @param roleName
     * @param roleCode
     * @param userName
     * @param userCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/conditional/query")
    public BaseApiResult pagePrivilege(@RequestParam(required = false) String privilegeName,
                                       @RequestParam(required = false) String privilegeType,
                                       @RequestParam(required = false) String privilegeCode,
                                       @RequestParam(required = false) String roleName,
                                       @RequestParam(required = false) String roleCode,
                                       @RequestParam(required = false) String userName,
                                       @RequestParam(required = false) String userCode,
                                       @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                       @RequestParam(required = false,defaultValue = "10") Integer pageSize
                                       ){
        try {
            Page<Privilege> page = privilegeService.multiConditionalPageQuery(privilegeName, privilegeType, privilegeCode,
                    roleName, roleCode, userName,
                    userCode, pageNum, pageSize);
            return BaseApiResult.success(page);
        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    @GetMapping("/queryAllPrivilege")
    public BaseApiResult queryAllPrivilege(){
        try {
            return BaseApiResult.success(privilegeService.list());
        }catch (Exception e) {
            log.error(e.getMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }


}
