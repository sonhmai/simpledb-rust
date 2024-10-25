package com.simpledb.buffer;

import com.simpledb.file.BlockId;
import com.simpledb.file.FileMgr;
import com.simpledb.log.LogMgr;

/**
 * A place for other components to access a disk block following protocol
 * 1. client asks buffer manager to pin a page from buffer pool to that block
 * 2. client works with content of the page
 * 3. when client is done, it tells buffer manager to unpin it.
 */
public class BufferMgr {
    private Buffer[] bufferpool;
    private int numAvailable;
    private static final long MAX_TIME = 10000; // 10 seconds

    /**
     * Creates a buffer manager having the specified number
     * of buffer slots.
     * This constructor depends on a {@link FileMgr} and
     * {@link com.simpledb.log.LogMgr LogMgr} object.
     * @param numbuffs the number of buffer slots to allocate
     */
    public BufferMgr(FileMgr fm, LogMgr lm, int numbuffs) {
        bufferpool = new Buffer[numbuffs];
        numAvailable = numbuffs;
        for (int i=0; i<numbuffs; i++)
            bufferpool[i] = new Buffer(fm, lm);
    }

    public synchronized Buffer pin(BlockId blk) {
        try {
            long timestamp = System.currentTimeMillis();
            Buffer buff = tryToPin(blk);
            while (buff == null && !waitingTooLong(timestamp)) {
                wait(MAX_TIME);
                buff = tryToPin(blk);
            }
            if (buff == null)
                throw new BufferAbortException();
            return buff;
        } catch (InterruptedException e) {
            throw new BufferAbortException();
        }
    }


    /**
     * Tries to pin a buffer to the specified block.
     * If there is already a buffer assigned to that block
     * then that buffer is used;
     * otherwise, an unpinned buffer from the pool is chosen.
     * Returns a null value if there are no available buffers.
     *
     * @param blk a reference to a disk block
     * @return the pinned buffer
     */
    private Buffer tryToPin(BlockId blk) {
        Buffer buff = findExistingBuffer(blk);
        if (buff == null) {
            buff = chooseUnpinnedBuffer();
            if (buff == null)
                return null;
            buff.assignToBlock(blk);
        }
        if (!buff.isPinned())
            numAvailable--;
        buff.pin();
        return buff;
    }

}
