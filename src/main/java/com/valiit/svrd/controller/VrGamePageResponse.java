package com.valiit.svrd.controller;

import com.valiit.svrd.dto.VRGameDTO;

import java.util.List;

public class VrGamePageResponse {
    private int totalPages;
    private List<VRGameDTO> data;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<VRGameDTO> getData() {
        return data;
    }

    public void setData(List<VRGameDTO> data) {
        this.data = data;
    }
}
