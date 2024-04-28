package com.lijinchao.entity.dto;

import com.lijinchao.entity.Category;
import com.lijinchao.entity.File;
import com.lijinchao.entity.License;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.Submission;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PaperDto extends Paper {
    private Subject subject;
    private License license;
    private Submission submission;
    private String subjectCategory;
    private Category primaryCategory;
    private List<Category> crossCategoryList = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();

}
