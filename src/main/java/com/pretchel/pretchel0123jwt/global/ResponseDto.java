package com.pretchel.pretchel0123jwt.global;

import lombok.Getter;

import java.util.List;

public class ResponseDto {
    private static final String OK_MSG = "정상적으로 처리되었습니다.";

    @Getter
    public static class Empty {
        private boolean success;
        private String message;

        public Empty() {
            success = true;
            message = OK_MSG;
        }
    }

    @Getter
    public static class Data<T> {
        private boolean success;
        private String message;
        private T data;

        public Data(T data) {
            success = true;
            message = OK_MSG;
            this.data = data;
        }
    }

    @Getter
    public static class DataList<T> {
        private boolean success;
        private String message;
        private List<T> data;
        private int totalCount;

        public DataList(List<T> data) {
            success = true;
            message = OK_MSG;
            this.data = data;
            totalCount = data.size();
        }
    }

    @Getter
    public static class Fail {
        private boolean success;
        private String message;

        public Fail(String message) {
            success = false;
            this.message = message;
        }
    }
}
