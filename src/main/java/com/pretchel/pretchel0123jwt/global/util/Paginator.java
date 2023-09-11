package com.pretchel.pretchel0123jwt.global.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Paginator {
    private Integer pagesPerBlock;
    private Integer profilesPerPage;
    private Long totalProfileCount;

    private Integer totalLastPageNum;

    public Paginator(Integer pagesPerBlock, Integer profilesPerPage, Long totalProfileCount) {
        this.pagesPerBlock = pagesPerBlock;
        this.profilesPerPage = profilesPerPage;
        this.totalProfileCount = totalProfileCount;

        this.setTotalLastPageNum();
    }

    public Integer getPagesPerBlock() {
        return pagesPerBlock;
    }

    public Integer getProfilesPerPage() {
        return profilesPerPage;
    }

    public Long getTotalProfileCount() {
        return totalProfileCount;
    }

    public Integer getTotalLastPageNum() {
        return totalLastPageNum;
    }

    public void setPagesPerBlock(Integer pagesPerBlock) {
        this.pagesPerBlock = pagesPerBlock;
    }

    public void setProfilesPerPage(Integer profilesPerPage) {
        this.profilesPerPage = profilesPerPage;
    }

    public void setTotalProfileCount(Long totalProfileCount) {
        this.totalProfileCount = totalProfileCount;
    }

    private void setTotalLastPageNum() {
        if(totalProfileCount == 0) {
            this.totalLastPageNum = 1;
        } else {
            this.totalLastPageNum = (int) (Math.ceil((double)totalProfileCount / profilesPerPage));
        }
    }

    private Map<String, Object> getBlock(Integer currentPageNum, Boolean isFixed) {
        if(profilesPerPage % 2 == 0 && !isFixed) {
            throw new IllegalStateException("profilesPerBlock은 홀수만 가능하다냥");

        }
        if(currentPageNum > totalLastPageNum && totalProfileCount != 0) {
            throw new IllegalStateException("currentPage가 총 페이지 수" + totalLastPageNum + "보다 크다니...!!");
        }

        Integer blockLastPageNum = totalLastPageNum;
        Integer blockFirstPageNum = 1;

        if(isFixed) {
            Integer mod = totalLastPageNum % pagesPerBlock;
            if(totalLastPageNum - mod >= currentPageNum) {
                blockLastPageNum = (int) (Math.ceil((float)currentPageNum / pagesPerBlock) * pagesPerBlock);
                blockFirstPageNum = blockLastPageNum - (pagesPerBlock - 1);
            } else {
                blockFirstPageNum = (int) (Math.ceil((float)currentPageNum / pagesPerBlock) * pagesPerBlock) - (pagesPerBlock - 1);
            }
        } else {
            Integer mid = pagesPerBlock / 2;

            if(currentPageNum <= pagesPerBlock) {
                blockLastPageNum = pagesPerBlock;
            } else if(currentPageNum < totalLastPageNum - mid) {
                blockLastPageNum = currentPageNum + mid;
            }

            blockFirstPageNum = blockLastPageNum - (pagesPerBlock - 1);

            if(totalLastPageNum < pagesPerBlock) {
                blockLastPageNum = totalLastPageNum;
                blockFirstPageNum = 1;
            }
        }

        List<Integer> pageList = new ArrayList<>();
        for(int i=0, val=blockFirstPageNum; val<=blockLastPageNum; i++, val++) {
            pageList.add(i, val);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isPrevExist", (int)currentPageNum > (int)pagesPerBlock);
        result.put("isNextExist", blockLastPageNum != 1 ? (int)blockLastPageNum != (int)totalLastPageNum : false);
        result.put("totalLastPageNum", totalLastPageNum);
        result.put("blockLastPageNum", blockLastPageNum);
        result.put("blockFirstPageNum", blockFirstPageNum);
        result.put("currentPageNum", currentPageNum);
        result.put("totalEventCount", totalProfileCount);
        result.put("pagesPerBlock", pagesPerBlock);
        result.put("eventsPerPage", profilesPerPage);
        result.put("pageList", pageList);

        return result;
    }

    public Map<String, Object> getElasticBlock(Integer currentPageNum) {
        return this.getBlock(currentPageNum, false);
    }

    public Map<String, Object> getFixedBlock(Integer currentPageNum) {
        return this.getBlock(currentPageNum, true);
    }
}
