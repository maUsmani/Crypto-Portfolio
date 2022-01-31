class TextBreak {
    void dash(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append("-"); // can be changed to print dash with multiples of number
            n--;
        }
        System.out.println(sb);
    }
    void dash() {  // For default length for line break
        int n = 5;
        StringBuilder sb = new StringBuilder();
        while(n > 0) {
            sb.append("-");
            n--;
        }
        System.out.println(sb);
    }
}