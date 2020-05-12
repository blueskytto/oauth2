package com.dreamsecurity.util;

public class Pagination {
	
	/** 한 페이지당 게시글 수 **/
    public static final int PAGE_SIZE = 10;
    
    /** 한 블럭(range)당 페이지 수 **/
    public static final int BLOCK_SIZE = 10;
    
    /** 현재 페이지 **/
    private int curPage;
        
    /** 총 페이지 수 **/
    private int totPage;
        
    /** 시작 페이지 **/
    private int startPage;
    
    /** 끝 페이지 **/
    private int endPage;
    
    /** 이전 페이지 **/
    private int prevPage;
    
    /** 다음 페이지 **/
    private int nextPage;
    
    /** 총 블럭 수 **/
    private int totBlock;
    
    /** 현재 블럭 **/
    private int curBlock;
    
    /** 이전 페이지 블럭 **/
    private int prevBlock;
    
    /** 다음 페이지 블럭 **/
    private int nextBlock;
    
    /** 현재 페이지 블럭의 시작번호 **/
    private int startBlock;
    
    /** 현재 페이지 블럭 끝 번호 **/
    private int endBlock;

    public Pagination(int listCnt, int curPage){
        
        /**
         * 페이징 처리 순서
         * 1. 총 페이지수
         * 2. 총 블럭(range)수
         * 3. range setting
         */
    	
    	curBlock = 1; // 현재 페이지 블록 번호
        this.curPage = curPage; // 현재 페이지 설정
        setTotPage(listCnt); // 전체 페이지 갯수 계산
        setPageRange();
        setTotBlock(); // 전체 페이지 블록 갯수 계산
        setBlockRange();
        
    }
    
    public void setBlockRange() {
    	// 현재 페이지가 몇번째 페이지 블록에 속하는지 계산
		curBlock = (int) Math.ceil((curPage - 1) / BLOCK_SIZE) + 1;
		// 현재 페이지 블록의 시작, 끝번호 계산
		startBlock = (curBlock - 1) * BLOCK_SIZE + 1;
		// 페이지 블록의 끝번호
		endBlock = startBlock + BLOCK_SIZE + 1;
		// 마지막 블록이 범위를 초과하지 않도록 계산
		if(endBlock > totPage) {
			endBlock = totPage;
		}
		// 이전을 눌렀을 때 이동할 페이지 번호
		prevPage = (curPage == 1) ? 1: (curBlock - 1) * BLOCK_SIZE;
		// 다음을 눌렀을 때 이동할 페이지 번호
		nextPage = curBlock > totBlock ? (curBlock * BLOCK_SIZE) : (curBlock * BLOCK_SIZE) + 1;
		// 마지막 페이지가 범위를 초과하지 않도록 처리
		if(nextPage >= totPage) nextPage = totPage;
		
	}

    public void setPageRange() {
    	startPage = (curPage - 1) * PAGE_SIZE + 1;
    	endPage = startPage + PAGE_SIZE - 1;
	}
    
	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getTotPage() {
		return totPage;
	}

	public void setTotPage(int count) {
		this.totPage = (int)Math.ceil(count * 1.0 / PAGE_SIZE);
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getTotBlock() {
		return totBlock;
	}

	public void setTotBlock() {
		this.totBlock = (int)Math.ceil(totPage / BLOCK_SIZE);
	}

	public int getCurBlock() {
		return curBlock;
	}

	public void setCurBlock(int curBlock) {
		this.curBlock = curBlock;
	}

	public int getPrevBlock() {
		return prevBlock;
	}

	public void setPrevBlock(int prevBlock) {
		this.prevBlock = prevBlock;
	}

	public int getNextBlock() {
		return nextBlock;
	}

	public void setNextBlock(int nextBlock) {
		this.nextBlock = nextBlock;
	}

	public int getStartBlock() {
		return startBlock;
	}

	public void setStartBlock(int startBlock) {
		this.startBlock = startBlock;
	}

	public int getEndBlock() {
		return endBlock;
	}

	public void setEndBlock(int endBlock) {
		this.endBlock = endBlock;
	}

	public static int getPageSize() {
		return PAGE_SIZE;
	}

	public static int getBlockSize() {
		return BLOCK_SIZE;
	}
    
    
}
