mod log_manager;
mod buffer_manager;
mod lsn;
mod buffer;

pub use log_manager::LogManager;
pub use buffer_manager::BufferManager;
pub use buffer::Buffer;

#[cfg(test)]
mod tests {
    use crate::simple_db::SimpleDB;
    use crate::filesystem::BlockId;

    #[test]
    fn buffer_test() {
        let db = SimpleDB::new("buffertest", 400, 3);
        let mut bm = db.buffer_mgr();

        // Pin first buffer
        let mut buff1 = bm.pin(BlockId::new("testfile", 1));
        let p = buff1.contents();
        let n = p.get_int(80);
        p.set_int(80, n + 1);
        buff1.set_modified(1, 0); // This modification will get written to disk.
        println!("The new value is {}", n + 1);
        bm.unpin(&mut buff1);

        // One of these pins will flush buff1 to disk:
        let mut buff2 = bm.pin(BlockId::new("testfile", 2));
        let mut buff3 = bm.pin(BlockId::new("testfile", 3));
        let mut buff4 = bm.pin(BlockId::new("testfile", 4));

        bm.unpin(&mut buff2);

        buff2 = bm.pin(BlockId::new("testfile", 1));
        let p2 = buff2.contents();
        p2.set_int(80, 9999);
        buff2.set_modified(1, 0);
        bm.unpin(&mut buff2);

        // We don't need to unpin buff3 and buff4 in this test,
        // but in a real scenario, we should unpin all buffers we've pinned.
        bm.unpin(&mut buff3);
        bm.unpin(&mut buff4);
    }
}