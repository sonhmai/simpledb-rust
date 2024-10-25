package simpledb.buffer;

import simpledb.file.BlockId;
import simpledb.file.FileMgr;
import simpledb.file.Page;
import simpledb.log.LogMgr;

/**
 * An individual buffer. A data-buffer wraps a page and stores information about
 * 1. its status,
 * 2. the associated disk block,
 * 3. number of times the buffer has been pinned,
 * 4. whether its contents have been modified,
 * 5. and if so, the id and lsn of the modifying transaction.
 *
 * @author Edward Sciore
 */
public class Buffer {
    private FileMgr fm;
    private LogMgr lm;
    private Page contents;
    /// Page does not store which block on disk the bytes mapped to
    private BlockId blk = null;
    /// number of times buffer has been pinned
    private int pins = 0;
    /// txn id transaction modified this buffer page
    private int txnum = -1;
    /// log sequence number transaction modified this buffer page
    private int lsn = -1;

    public Buffer(FileMgr fm, LogMgr lm) {
        this.fm = fm;
        this.lm = lm;
        contents = new Page(fm.blockSize());
    }

    public Page contents() {
        return contents;
    }

    /**
     * Returns a reference to the disk block
     * allocated to the buffer.
     *
     * @return a reference to a disk block
     */
    public BlockId block() {
        return blk;
    }

    public void setModified(int txnum, int lsn) {
        this.txnum = txnum;
        if (lsn >= 0)
            this.lsn = lsn;
    }

    /**
     * Return true if the buffer is currently pinned
     * (that is, if it has a nonzero pin count).
     *
     * @return true if the buffer is pinned
     */
    public boolean isPinned() {
        return pins > 0;
    }

    public int modifyingTx() {
        return txnum;
    }

    /**
     * Reads the contents of the specified block into
     * the contents of the buffer.
     * If the buffer was dirty, then its previous contents
     * are first written to disk.
     *
     * @param b a reference to the data block
     */
    void assignToBlock(BlockId b) {
        flush();
        blk = b;
        fm.read(blk, contents);
        pins = 0;
    }

    /**
     * Write the buffer to its disk block if it is dirty.
     */
    void flush() {
        if (txnum >= 0) {
            lm.flush(lsn);
            fm.write(blk, contents);
            txnum = -1;
        }
    }

    /**
     * Increase the buffer's pin count.
     */
    void pin() {
        pins++;
    }

    /**
     * Decrease the buffer's pin count.
     */
    void unpin() {
        pins--;
    }
}
