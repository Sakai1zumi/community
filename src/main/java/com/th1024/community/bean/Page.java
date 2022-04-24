package com.th1024.community.bean;

/**
 * 封装分页相关信息
 *
 * @author izumisakai
 * @create 2022-04-21 17:19
 */
public class Page {

    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数（用于计算总页码）
    private int rows;
    //查询路径（用于复用分页链接）
    private String url;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) { //对数据进行必要的限制
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        //current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * 获取终止页码
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();

        return Math.min(to, total);
    }
}
