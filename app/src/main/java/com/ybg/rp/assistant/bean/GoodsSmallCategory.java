package com.ybg.rp.assistant.bean;

/**
 * 小类别数据
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.bean
 * @修改记录:
 *
 * @date 2016/2/24 0024
 */
public class GoodsSmallCategory {

    /**
     * id : 1                   -->小类别ID
     * category : 功能饮料      -->类别名称
     * categoryType : 1
     * createDate : 1449710634000
     * createUser : 1
     * des : 1
     * keyword : 1
     * goodBigCategory : {"id":1,"category":"饮料","categoryType":1,"createDate":1450060014000,"createUser":"wj","des":"1","keyword":"饮料","sort":1,"status":1,"updateDate":1450060018000,"updateUser":"wj"}
     * sort : 1
     * status : 1
     * updateDate : 1449710640000
     * upateUser : 1
     */

    private String id;
    private String category;
    private String categoryType;
    private String createDate;
    private String createUser;
    private String des;
    private String keyword;
    /**
     * id : 1
     * category : 饮料
     * categoryType : 1
     * createDate : 1450060014000
     * createUser : wj
     * des : 1
     * keyword : 饮料
     * sort : 1
     * status : 1
     * updateDate : 1450060018000
     * updateUser : wj
     */

    private GoodBigCategoryEntity goodBigCategory;
    private String sort;
    private String status;
    private String updateDate;
    private String upateUser;

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setGoodBigCategory(GoodBigCategoryEntity goodBigCategory) {
        this.goodBigCategory = goodBigCategory;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public void setUpateUser(String upateUser) {
        this.upateUser = upateUser;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public String getDes() {
        return des;
    }

    public String getKeyword() {
        return keyword;
    }

    public GoodBigCategoryEntity getGoodBigCategory() {
        return goodBigCategory;
    }

    public String getSort() {
        return sort;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getUpateUser() {
        return upateUser;
    }

    //暂时没有用到
    public static class GoodBigCategoryEntity {
        private int id;
        private String category;
        private int categoryType;
        private long createDate;
        private String createUser;
        private String des;
        private String keyword;
        private int sort;
        private int status;
        private long updateDate;
        private String updateUser;

        public void setId(int id) {
            this.id = id;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setCategoryType(int categoryType) {
            this.categoryType = categoryType;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setUpdateDate(long updateDate) {
            this.updateDate = updateDate;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public int getId() {
            return id;
        }

        public String getCategory() {
            return category;
        }

        public int getCategoryType() {
            return categoryType;
        }

        public long getCreateDate() {
            return createDate;
        }

        public String getCreateUser() {
            return createUser;
        }

        public String getDes() {
            return des;
        }

        public String getKeyword() {
            return keyword;
        }

        public int getSort() {
            return sort;
        }

        public int getStatus() {
            return status;
        }

        public long getUpdateDate() {
            return updateDate;
        }

        public String getUpdateUser() {
            return updateUser;
        }
    }
}
