public class BranchHoisting {
    public int original(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            if (n == 0) {
               x = y * 2;
            } else {
                x = y * 3;
            }
            y++;
        }
        return x;
    }

    public int expected(int n) {
        int y = 0;
        int ynext = 0;
        int x = 0;
        while (ynext < 500) {
            y = ynext;
            ynext++;
        }
        
        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
        }

        return x;
    }
}
