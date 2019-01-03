
package com.hkstlr.blogbox.control;


/**
 *
 * @author henry.kastler
 */
public final class Paginator {
	
	public Paginator(){};
    
    public Paginator(int pageSize, int page, int numberOfItems, int numberOfPages){
        setPageSize(pageSize);
        setPage(page);
        setNumberOfItems(numberOfItems);
        setNumberOfPages(numberOfPages);
    }
    
    public Paginator(int pageSize, int page, int numberOfItems){
        setPageSize(pageSize);
        setPage(page);
        setNumberOfItems(numberOfItems);
        setNumberOfPages();
    }
    
    private int pageSize = 24;
    //pages have a 1 based index
    //list elements have 0 based index
    private int page = 1;
    private int numberOfPages = 1;
    private int numberOfItems = 0;
    
    
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
    
    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
    
    public void setNumberOfPages(){
      this.numberOfPages = (int) Math.ceil((double) getNumberOfItems() / getPageSize());
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
    
    public int getPageFirstItem() {       
        return getPageFirstItem(this.page ,this.pageSize);
    }
    
    public int getPageFirstItem(int page, int pageSize) {
          
    	  int pageFirstItem = ((page * pageSize) - pageSize) + 1;
          if ( pageFirstItem < 0 )  {
        	  pageFirstItem = 1;
          };
        
        return pageFirstItem;
    }

    public int getPageLastItem() {
        return getPageLastItem(getPageFirstItem(),this.pageSize,this.numberOfItems);
    }
    
    public int getPageLastItem(int firstPageItem, int pageSize, int numberOfItems) {
    	
    	int lastItemIndex = (firstPageItem-1) + pageSize;        
        int count = numberOfItems;
        //log.log(Level.INFO,"count:{0}",Integer.toString(count));
        if (lastItemIndex > count) {
        	lastItemIndex = count;
        }
        if (lastItemIndex < 0) {
        	lastItemIndex = 0;
        }
        
        //System.out.println(itemIndex);
        return lastItemIndex;
    }

    public boolean isHasNextPage() {        
        return (page * pageSize)  + 1 <= numberOfItems;
    }

    public void nextPage() {
        if (isHasNextPage()) {
            page++;
        }
    }

    public boolean isHasPreviousPage() {
        return page-1 > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            page--;
        }
    }

}
