class LoopPeeling {
    public int original (int N) {
        int x = 0;
        int i = 0;
        while (i < N) {
            x += 5;
            i++;
        }
        return x;
    }

    public int expected (int N) {
        int x = 0;
        if (0 >= N) {
            x = 0;
        } else {
            x = 5;
            int i = 1;
            while (i < N) {
                x += 5;
                i++;
            }
        }
        return x;
    }
}