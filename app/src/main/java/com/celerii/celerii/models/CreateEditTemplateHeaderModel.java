package com.celerii.celerii.models;

public class CreateEditTemplateHeaderModel {
    String templateTitle;

    public CreateEditTemplateHeaderModel() {
        this.templateTitle = "";
    }

    public CreateEditTemplateHeaderModel(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }
}
