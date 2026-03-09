import java.util.*;

// ============================================================
//  SkyCast - Instant Weather Predictor
//  Java Console Application
//  CO1: Searching (Linear + Binary) + Sorting (5 algorithms)
//  CO2: Singly Linked List  (Recent Search History)
//  CO3: Stack + Circular Queue + Priority Queue
//  CO4: HashMap for O(1) city lookup
//  CO5: Full working practical application
//  CO6: Menu-driven, runnable Java program
// ============================================================

public class SkyCast {

    // ========================================================
    //  DATA MODEL
    // ========================================================

    static class ForecastDay {
        String day, desc;
        int wid, hi, lo, hum, rain;
        ForecastDay(String day, int wid, String desc,
                    int hi, int lo, int hum, int rain) {
            this.day  = day;  this.wid  = wid;  this.desc = desc;
            this.hi   = hi;   this.lo   = lo;
            this.hum  = hum;  this.rain = rain;
        }
    }

    static class City {
        String name, country, desc;
        int temp, feels, tmin, tmax, hum, pres, vis, clouds, wid;
        double lat, lon, wind;
        long rise, set, tz;
        ForecastDay[] week;

        City(String name, String country, double lat, double lon,
             int temp, int feels, int tmin, int tmax,
             int hum, double wind, int pres, int vis,
             int clouds, String desc, int wid,
             long rise, long set, long tz, ForecastDay[] week) {
            this.name    = name;    this.country = country;
            this.lat     = lat;     this.lon     = lon;
            this.temp    = temp;    this.feels   = feels;
            this.tmin    = tmin;    this.tmax    = tmax;
            this.hum     = hum;     this.wind    = wind;
            this.pres    = pres;    this.vis     = vis;
            this.clouds  = clouds;  this.desc    = desc;
            this.wid     = wid;     this.rise    = rise;
            this.set     = set;     this.tz      = tz;
            this.week    = week;
        }
    }

    // ========================================================
    //  CO2 — SINGLY LINKED LIST  (Recent Search History)
    //  insertFront  O(1)
    //  removeLast   O(n)
    //  traverse     O(n)
    // ========================================================

    static class Node {
        String data;
        Node   next;
        Node(String d) { data = d; next = null; }
    }

    static class RecentList {
        Node head;
        int  MAX = 5;

        void add(String city) {
            // Remove duplicate — O(n)
            Node prev = null, cur = head;
            while (cur != null) {
                if (cur.data.equalsIgnoreCase(city)) {
                    if (prev == null) head = cur.next;
                    else              prev.next = cur.next;
                    break;
                }
                prev = cur; cur = cur.next;
            }
            // Insert at front — O(1)
            Node n = new Node(city); n.next = head; head = n;
            // Trim to MAX
            if (size() > MAX) removeLast();
        }

        void removeLast() {
            if (head == null) return;
            if (head.next == null) { head = null; return; }
            Node cur = head;
            while (cur.next.next != null) cur = cur.next;
            cur.next = null;
        }

        int size() {
            int c = 0; Node cur = head;
            while (cur != null) { c++; cur = cur.next; }
            return c;
        }

        void print() {
            if (head == null) {
                System.out.println("  (none yet)"); return;
            }
            Node cur = head; int i = 1;
            while (cur != null) {
                System.out.println("  [" + i + "] " + cur.data);
                cur = cur.next; i++;
            }
        }
    }

    // ========================================================
    //  CO3a — ARRAY STACK  (Search Undo History)
    //  push O(1)   pop O(1)   peek O(1)
    // ========================================================

    static class SearchStack {
        String[] arr = new String[50];
        int      top = -1;

        void    push(String s) { if (top < 49) arr[++top] = s; }
        String  pop()          { return top >= 0 ? arr[top--] : null; }
        String  peek()         { return top >= 0 ? arr[top]   : null; }
        boolean isEmpty()      { return top == -1; }

        void print() {
            if (isEmpty()) { System.out.println("  Stack is empty"); return; }
            for (int i = top; i >= 0; i--)
                System.out.println("  [" + (top - i + 1) + "] " + arr[i]);
        }
    }

    // ========================================================
    //  CO3b — CIRCULAR QUEUE  (Weather Alert Ticker)
    //  enqueue O(1)   dequeue O(1)   fixed-size array
    // ========================================================

    static class AlertQueue {
        String[] arr;
        int front, rear, size, cap;

        AlertQueue(int cap) {
            this.cap = cap; arr = new String[cap];
            front = 0; rear = -1; size = 0;
        }

        void enqueue(String s) {
            if (size == cap) dequeue();          // overwrite oldest
            rear = (rear + 1) % cap;
            arr[rear] = s; size++;
        }

        String dequeue() {
            if (size == 0) return null;
            String s = arr[front];
            front = (front + 1) % cap;
            size--; return s;
        }

        void printAll() {
            if (size == 0) { System.out.println("  No active alerts"); return; }
            int idx = front;
            for (int i = 0; i < size; i++) {
                System.out.println("  >> " + arr[idx]);
                idx = (idx + 1) % cap;
            }
        }
    }

    // ========================================================
    //  CO3c — MAX HEAP ENTRY  (City Heat Ranking)
    //  Uses Java PriorityQueue — insert O(log n), poll O(log n)
    // ========================================================

    static class CityHeat implements Comparable<CityHeat> {
        String name; int temp;
        CityHeat(String n, int t) { name = n; temp = t; }
        public int compareTo(CityHeat o) { return o.temp - this.temp; } // max-heap
    }

    // ========================================================
    //  CO1 — SEARCHING ALGORITHMS
    // ========================================================

    /** Linear Search — O(n) worst case */
    static int linearSearch(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i].equalsIgnoreCase(target)) return i;
        return -1;
    }

    /** Binary Search — O(log n)  [array must be sorted first] */
    static int binarySearch(String[] sorted, String target) {
        int lo = 0, hi = sorted.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int cmp = sorted[mid].compareToIgnoreCase(target);
            if      (cmp == 0) return mid;
            else if (cmp <  0) lo = mid + 1;
            else               hi = mid - 1;
        }
        return -1;
    }

    // ========================================================
    //  CO1 — SORTING ALGORITHMS
    // ========================================================

    /** Bubble Sort — O(n^2) */
    static void bubbleSort(String[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (a[j].compareToIgnoreCase(a[j + 1]) > 0) {
                    String t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                }
    }

    /** Selection Sort — O(n^2) */
    static void selectionSort(String[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++)
                if (a[j].compareToIgnoreCase(a[min]) < 0) min = j;
            String t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    /** Insertion Sort — O(n^2) worst, O(n) best */
    static void insertionSort(String[] a) {
        for (int i = 1; i < a.length; i++) {
            String key = a[i]; int j = i - 1;
            while (j >= 0 && a[j].compareToIgnoreCase(key) > 0) {
                a[j + 1] = a[j]; j--;
            }
            a[j + 1] = key;
        }
    }

    /** Merge Sort — O(n log n) divide and conquer */
    static void mergeSort(String[] a, int l, int r) {
        if (l >= r) return;
        int m = (l + r) / 2;
        mergeSort(a, l, m); mergeSort(a, m + 1, r);
        merge(a, l, m, r);
    }
    static void merge(String[] a, int l, int m, int r) {
        String[] L = Arrays.copyOfRange(a, l, m + 1);
        String[] R = Arrays.copyOfRange(a, m + 1, r + 1);
        int i = 0, j = 0, k = l;
        while (i < L.length && j < R.length)
            a[k++] = (L[i].compareToIgnoreCase(R[j]) <= 0) ? L[i++] : R[j++];
        while (i < L.length) a[k++] = L[i++];
        while (j < R.length) a[k++] = R[j++];
    }

    /** Quick Sort — O(n log n) avg, O(n^2) worst */
    static void quickSort(String[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = partition(a, lo, hi);
        quickSort(a, lo, p - 1); quickSort(a, p + 1, hi);
    }
    static int partition(String[] a, int lo, int hi) {
        String pivot = a[hi]; int i = lo - 1;
        for (int j = lo; j < hi; j++)
            if (a[j].compareToIgnoreCase(pivot) < 0) {
                i++; String t = a[i]; a[i] = a[j]; a[j] = t;
            }
        String t = a[i + 1]; a[i + 1] = a[hi]; a[hi] = t;
        return i + 1;
    }

    // ========================================================
    //  CO4 — HASHMAP  city store — O(1) average lookup
    // ========================================================

    static HashMap<String, City> CITIES = new HashMap<>();

    // ========================================================
    //  DISPLAY HELPERS
    // ========================================================

    static String getIcon(int wid) {
        if (wid >= 200 && wid < 300) return "[Thunder]";
        if (wid >= 300 && wid < 400) return "[Drizzle]";
        if (wid >= 500 && wid < 510) return "[Rain   ]";
        if (wid >= 510 && wid < 600) return "[Storm  ]";
        if (wid >= 600 && wid < 700) return "[Snow   ]";
        if (wid >= 700 && wid < 800) return "[Fog    ]";
        if (wid == 800)              return "[Sunny  ]";
        if (wid == 801)              return "[FewCld ]";
        if (wid == 802)              return "[PrtCld ]";
        if (wid == 803)              return "[MstCld ]";
        if (wid == 804)              return "[Cloudy ]";
        return                              "[Weather]";
    }

    static String fmtTemp(int c, boolean fah) {
        return fah ? (int)(c * 9.0 / 5 + 32) + " F" : c + " C";
    }

    static String fmtTime(long unix, long tz) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis((unix + tz) * 1000L);
        return String.format("%02d:%02d",
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    static void line(char ch, int len) {
        System.out.println(String.valueOf(ch).repeat(len));
    }

    static void header(String title) {
        System.out.println();
        line('=', 65);
        int pad = Math.max(0, (65 - title.length()) / 2);
        System.out.println(" ".repeat(pad) + title);
        line('=', 65);
    }

    // ========================================================
    //  DISPLAY WEATHER  (same sections as the HTML page)
    // ========================================================

    static void displayWeather(City c, boolean fah) {
        header("SKYCAST  -  " + c.name + ", " + c.country);

        System.out.printf("  Location    : %s, %s  |  Lat: %.2f  Lon: %.2f%n",
            c.name, c.country, c.lat, c.lon);
        System.out.printf("  Condition   : %s  %s%n", getIcon(c.wid), c.desc);
        System.out.printf("  Temperature : %s%n", fmtTemp(c.temp, fah));
        System.out.printf("  Feels Like  : %s%n", fmtTemp(c.feels, fah));

        line('-', 65);
        System.out.println("  WEATHER STATS");
        line('-', 65);
        System.out.printf("  Humidity    : %d %%%n",    c.hum);
        System.out.printf("  Wind Speed  : %.1f m/s%n", c.wind);
        System.out.printf("  Pressure    : %d hPa%n",   c.pres);
        System.out.printf("  Visibility  : %d km%n",    c.vis);

        line('-', 65);
        System.out.println("  SUN & SKY");
        line('-', 65);
        System.out.printf("  Sunrise     : %s%n", fmtTime(c.rise, c.tz));
        System.out.printf("  Sunset      : %s%n", fmtTime(c.set,  c.tz));
        System.out.printf("  Cloud Cover : %d %%%n", c.clouds);

        line('-', 65);
        System.out.println("  TEMPERATURE RANGE");
        line('-', 65);
        System.out.printf("  Max Temp    : %s%n", fmtTemp(c.tmax, fah));
        System.out.printf("  Min Temp    : %s%n", fmtTemp(c.tmin, fah));

        line('=', 65);
        System.out.println("  7-DAY WEATHER FORECAST");
        line('-', 65);
        System.out.printf("  %-12s %-10s %-18s %-8s %-8s %s%n",
            "Day","Condition","Description","High","Low","Rain");
        line('-', 65);

        String[] DAY_NAMES = {"Sunday","Monday","Tuesday","Wednesday",
                               "Thursday","Friday","Saturday"};
        int todayIdx = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < c.week.length; i++) {
            ForecastDay f  = c.week[i];
            String dayLabel = i == 0 ? "Today"
                            : i == 1 ? "Tomorrow"
                            : DAY_NAMES[(todayIdx + i) % 7];
            String rain     = f.rain > 0 ? f.rain + " mm" : "--";
            System.out.printf("  %-12s %-10s %-18s %-8s %-8s %s%n",
                dayLabel, getIcon(f.wid), f.desc,
                fmtTemp(f.hi, fah), fmtTemp(f.lo, fah), rain);
        }
        line('=', 65);
    }

    // ========================================================
    //  CO3c — PRIORITY QUEUE: Rank cities by temperature
    // ========================================================

    static void showHeatRanking() {
        header("HEAT RANKING  -  Priority Queue  (CO3)");
        System.out.println("  Max-Heap: hottest city is always at the top");
        System.out.println("  insert O(log n)   poll O(log n)");
        line('-', 65);

        PriorityQueue<CityHeat> pq = new PriorityQueue<>();
        for (City c : CITIES.values()) pq.add(new CityHeat(c.name, c.temp));

        int rank = 1;
        while (!pq.isEmpty()) {
            CityHeat ch = pq.poll();
            System.out.printf("  Rank %2d :  %-15s  %d C%n",
                rank++, ch.name, ch.temp);
        }
        line('=', 65);
    }

    // ========================================================
    //  CO1 — SORTING + SEARCHING DEMO
    // ========================================================

    static void showSortingDemo() {
        header("SORTING & SEARCHING DEMO  (CO1)");
        String[] names = CITIES.keySet().toArray(new String[0]);
        String[] copy;

        System.out.println("  Sorting 12 city names:");
        line('-', 65);

        copy = names.clone(); bubbleSort(copy);
        System.out.print("  Bubble    O(n^2)   : ");
        for (String s : copy) System.out.print(s.substring(0, Math.min(3, s.length())) + " ");
        System.out.println();

        copy = names.clone(); selectionSort(copy);
        System.out.print("  Selection O(n^2)   : ");
        for (String s : copy) System.out.print(s.substring(0, Math.min(3, s.length())) + " ");
        System.out.println();

        copy = names.clone(); insertionSort(copy);
        System.out.print("  Insertion O(n^2/n) : ");
        for (String s : copy) System.out.print(s.substring(0, Math.min(3, s.length())) + " ");
        System.out.println();

        copy = names.clone(); mergeSort(copy, 0, copy.length - 1);
        System.out.print("  Merge     O(nlogn) : ");
        for (String s : copy) System.out.print(s.substring(0, Math.min(3, s.length())) + " ");
        System.out.println();

        copy = names.clone(); quickSort(copy, 0, copy.length - 1);
        System.out.print("  Quick     O(nlogn) : ");
        for (String s : copy) System.out.print(s.substring(0, Math.min(3, s.length())) + " ");
        System.out.println();

        line('-', 65);
        System.out.println("  Search for 'mumbai':");
        String target = "mumbai";
        int li = linearSearch(names, target);

        String[] sorted = names.clone(); mergeSort(sorted, 0, sorted.length - 1);
        int bi = binarySearch(sorted, target);

        System.out.printf("  Linear Search -> index %d   O(n)      scanned all%n", li);
        System.out.printf("  Binary Search -> index %d   O(log n)  only %d steps%n",
            bi, (int)(Math.log(names.length) / Math.log(2)) + 1);

        // Empirical time comparison
        long t0 = System.nanoTime();
        for (int r = 0; r < 100000; r++) linearSearch(names, target);
        long linTime = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int r = 0; r < 100000; r++) binarySearch(sorted, target);
        long binTime = System.nanoTime() - t0;

        line('-', 65);
        System.out.println("  Empirical timing (100,000 runs each):");
        System.out.printf("  Linear Search : %d ms%n", linTime / 1_000_000);
        System.out.printf("  Binary Search : %d ms%n", binTime / 1_000_000);
        System.out.println("  Binary is faster because O(log n) < O(n)");
        line('=', 65);
    }

    // ========================================================
    //  CO3b — AUTO-GENERATE ALERTS based on city data
    // ========================================================

    static void generateAlerts(City c, AlertQueue alerts) {
        if (c.temp  > 35) alerts.enqueue("HEAT    : " + c.name + " is " + c.temp + " C. Stay hydrated!");
        if (c.hum   > 85) alerts.enqueue("HUMID   : " + c.name + " humidity " + c.hum + "%. Very sticky.");
        if (c.wind  >  8) alerts.enqueue("WIND    : " + c.name + " wind " + c.wind + " m/s. Hold tight!");
        if (c.vis   <  5) alerts.enqueue("VIS     : " + c.name + " visibility " + c.vis + " km. Drive slow.");
        if (c.clouds> 80) alerts.enqueue("CLOUD   : " + c.name + " is " + c.clouds + "% cloudy today.");
        for (ForecastDay f : c.week)
            if (f.rain > 20)
                alerts.enqueue("RAIN    : Heavy rain " + f.rain + " mm on " + f.day + " in " + c.name);
    }

    // ========================================================
    //  CO4 — HASHMAP INFO
    // ========================================================

    static void showHashMapInfo() {
        header("HASHMAP INFO  (CO4)  -  City Lookup Table");
        System.out.println("  Structure : HashMap<String, City>");
        System.out.println("  Lookup    : O(1) average using hash function");
        System.out.println("  Cities    : " + CITIES.size());
        line('-', 65);
        List<String> keys = new ArrayList<>(CITIES.keySet());
        Collections.sort(keys);
        int i = 1;
        for (String k : keys)
            System.out.printf("  %2d. Key: %-15s -> %s, %s%n",
                i++, k, CITIES.get(k).name, CITIES.get(k).country);
        line('=', 65);
    }

    // ========================================================
    //  CITY DATABASE  — same 12 cities as the HTML version
    // ========================================================

    static void loadCities() {

        CITIES.put("tokyo", new City("Tokyo", "JP", 35.68, 139.76,
            18, 16, 14, 21, 65, 4.2, 1015, 10, 30, "Partly Cloudy", 802,
            1708301400L, 1708343200L, 32400L, new ForecastDay[]{
            new ForecastDay("Today",     800, "Clear Sky",     21, 12, 55, 0),
            new ForecastDay("Tomorrow",  802, "Partly Cloudy", 19, 13, 60, 0),
            new ForecastDay("Tuesday",   500, "Light Rain",    16, 11, 78, 6),
            new ForecastDay("Wednesday", 804, "Overcast",      15, 10, 82, 2),
            new ForecastDay("Thursday",  802, "Partly Cloudy", 18, 12, 63, 0),
            new ForecastDay("Friday",    800, "Sunny",         22, 13, 50, 0),
            new ForecastDay("Saturday",  801, "Few Clouds",    20, 12, 57, 0)}));

        CITIES.put("london", new City("London", "GB", 51.51, -0.13,
            9, 6, 6, 11, 82, 7.8, 998, 8, 88, "Overcast Clouds", 804,
            1708322400L, 1708360000L, 0L, new ForecastDay[]{
            new ForecastDay("Today",     500, "Light Rain",    10,  5, 85,  8),
            new ForecastDay("Tomorrow",  804, "Overcast",       9,  4, 80,  2),
            new ForecastDay("Tuesday",   500, "Moderate Rain",  8,  4, 88, 14),
            new ForecastDay("Wednesday", 804, "Cloudy",        10,  5, 78,  1),
            new ForecastDay("Thursday",  802, "Partly Cloudy", 12,  6, 72,  0),
            new ForecastDay("Friday",    800, "Sunny",         13,  6, 65,  0),
            new ForecastDay("Saturday",  801, "Few Clouds",    11,  5, 70,  0)}));

        CITIES.put("new york", new City("New York", "US", 40.71, -74.01,
            5, 1, 2, 8, 70, 9.1, 1020, 16, 20, "Clear Sky", 800,
            1708341000L, 1708380000L, -18000L, new ForecastDay[]{
            new ForecastDay("Today",     800, "Clear Sky",      8,  1, 65,  0),
            new ForecastDay("Tomorrow",  801, "Few Clouds",     7,  0, 68,  0),
            new ForecastDay("Tuesday",   600, "Light Snow",     3, -2, 80,  0),
            new ForecastDay("Wednesday", 804, "Overcast",       5, -1, 75,  3),
            new ForecastDay("Thursday",  802, "Partly Cloudy",  9,  2, 62,  0),
            new ForecastDay("Friday",    800, "Sunny",         11,  3, 55,  0),
            new ForecastDay("Saturday",  500, "Light Rain",     8,  2, 72,  5)}));

        CITIES.put("dubai", new City("Dubai", "AE", 25.20, 55.27,
            28, 28, 23, 32, 45, 3.5, 1012, 10, 5, "Sunny", 800,
            1708307400L, 1708349000L, 14400L, new ForecastDay[]{
            new ForecastDay("Today",     800, "Sunny",          32, 22, 42,  0),
            new ForecastDay("Tomorrow",  800, "Clear Sky",      33, 23, 40,  0),
            new ForecastDay("Tuesday",   801, "Few Clouds",     31, 22, 45,  0),
            new ForecastDay("Wednesday", 800, "Sunny",          34, 24, 38,  0),
            new ForecastDay("Thursday",  802, "Partly Cloudy",  30, 21, 50,  0),
            new ForecastDay("Friday",    800, "Sunny",          33, 23, 42,  0),
            new ForecastDay("Saturday",  800, "Clear and Hot",  35, 25, 36,  0)}));

        CITIES.put("sydney", new City("Sydney", "AU", -33.87, 151.21,
            26, 25, 21, 29, 68, 5.2, 1018, 10, 45, "Scattered Clouds", 802,
            1708282000L, 1708330000L, 39600L, new ForecastDay[]{
            new ForecastDay("Today",     802, "Scattered Clouds", 28, 20, 65,  0),
            new ForecastDay("Tomorrow",  800, "Sunny",            30, 21, 60,  0),
            new ForecastDay("Tuesday",   500, "Light Rain",       24, 18, 80, 10),
            new ForecastDay("Wednesday", 804, "Cloudy",           22, 17, 78,  4),
            new ForecastDay("Thursday",  802, "Partly Cloudy",    25, 18, 68,  0),
            new ForecastDay("Friday",    800, "Clear Sky",        29, 20, 58,  0),
            new ForecastDay("Saturday",  801, "Few Clouds",       27, 19, 62,  0)}));

        CITIES.put("mumbai", new City("Mumbai", "IN", 19.08, 72.88,
            32, 36, 28, 34, 78, 3.8, 1008, 6, 60, "Humid and Hazy", 721,
            1708296600L, 1708338600L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     721, "Hazy",           34, 27, 78,  0),
            new ForecastDay("Tomorrow",  500, "Light Rain",     31, 26, 85,  8),
            new ForecastDay("Tuesday",   502, "Heavy Rain",     28, 25, 92, 32),
            new ForecastDay("Wednesday", 500, "Moderate Rain",  29, 25, 88, 15),
            new ForecastDay("Thursday",  721, "Hazy",           33, 27, 80,  0),
            new ForecastDay("Friday",    800, "Sunny",          35, 28, 72,  0),
            new ForecastDay("Saturday",  802, "Partly Cloudy",  33, 27, 75,  0)}));

        CITIES.put("delhi", new City("Delhi", "IN", 28.61, 77.21,
            22, 20, 15, 26, 55, 4.5, 1014, 5, 25, "Partly Cloudy", 802,
            1708294200L, 1708335000L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     802, "Partly Cloudy",   26, 14, 55, 0),
            new ForecastDay("Tomorrow",  800, "Sunny and Clear", 28, 15, 48, 0),
            new ForecastDay("Tuesday",   721, "Hazy",            27, 15, 60, 0),
            new ForecastDay("Wednesday", 804, "Overcast",        24, 13, 65, 2),
            new ForecastDay("Thursday",  500, "Light Rain",      22, 13, 72, 6),
            new ForecastDay("Friday",    802, "Partly Cloudy",   25, 14, 58, 0),
            new ForecastDay("Saturday",  800, "Clear Sky",       27, 15, 50, 0)}));

        CITIES.put("bangalore", new City("Bangalore", "IN", 12.97, 77.59,
            26, 25, 20, 29, 60, 2.9, 1016, 9, 35, "Pleasant and Clear", 801,
            1708298400L, 1708340000L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     801, "Few Clouds",    29, 19, 58, 0),
            new ForecastDay("Tomorrow",  800, "Pleasant",      30, 20, 55, 0),
            new ForecastDay("Tuesday",   500, "Light Rain",    25, 18, 75, 8),
            new ForecastDay("Wednesday", 802, "Partly Cloudy", 27, 19, 62, 0),
            new ForecastDay("Thursday",  500, "Light Showers", 24, 18, 78, 5),
            new ForecastDay("Friday",    800, "Sunny",         29, 20, 55, 0),
            new ForecastDay("Saturday",  801, "Few Clouds",    28, 19, 58, 0)}));

        CITIES.put("chennai", new City("Chennai", "IN", 13.08, 80.27,
            34, 38, 29, 36, 80, 5.1, 1007, 7, 50, "Hot and Humid", 721,
            1708297800L, 1708339200L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     721, "Hot and Humid",  36, 28, 80,  0),
            new ForecastDay("Tomorrow",  800, "Sunny and Hot",  37, 29, 75,  0),
            new ForecastDay("Tuesday",   500, "Light Rain",     33, 27, 88, 10),
            new ForecastDay("Wednesday", 804, "Cloudy",         32, 27, 85,  3),
            new ForecastDay("Thursday",  500, "Moderate Rain",  31, 26, 90, 18),
            new ForecastDay("Friday",    802, "Partly Cloudy",  34, 28, 78,  0),
            new ForecastDay("Saturday",  800, "Sunny",          36, 28, 75,  0)}));

        CITIES.put("kolkata", new City("Kolkata", "IN", 22.57, 88.36,
            29, 32, 24, 31, 72, 3.2, 1010, 8, 40, "Warm and Cloudy", 803,
            1708293000L, 1708333800L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     803, "Mostly Cloudy",  31, 23, 72,  0),
            new ForecastDay("Tomorrow",  500, "Light Rain",     29, 23, 82,  9),
            new ForecastDay("Tuesday",   502, "Heavy Shower",   27, 22, 90, 28),
            new ForecastDay("Wednesday", 804, "Overcast",       28, 22, 85,  5),
            new ForecastDay("Thursday",  802, "Partly Cloudy",  30, 23, 75,  0),
            new ForecastDay("Friday",    800, "Clear and Warm", 32, 24, 68,  0),
            new ForecastDay("Saturday",  801, "Few Clouds",     31, 23, 70,  0)}));

        CITIES.put("hyderabad", new City("Hyderabad", "IN", 17.38, 78.49,
            28, 27, 22, 31, 58, 3.6, 1013, 9, 20, "Mostly Sunny", 801,
            1708297200L, 1708338600L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     801, "Mostly Sunny",  31, 21, 55, 0),
            new ForecastDay("Tomorrow",  800, "Sunny",         33, 22, 50, 0),
            new ForecastDay("Tuesday",   802, "Partly Cloudy", 30, 21, 58, 0),
            new ForecastDay("Wednesday", 500, "Light Rain",    27, 20, 72, 6),
            new ForecastDay("Thursday",  804, "Overcast",      26, 20, 75, 3),
            new ForecastDay("Friday",    802, "Partly Cloudy", 29, 21, 60, 0),
            new ForecastDay("Saturday",  800, "Clear Sky",     32, 22, 52, 0)}));

        CITIES.put("jaipur", new City("Jaipur", "IN", 26.92, 75.82,
            25, 23, 18, 28, 42, 4.0, 1015, 10, 10, "Clear Sky", 800,
            1708294800L, 1708336000L, 19800L, new ForecastDay[]{
            new ForecastDay("Today",     800, "Clear Sky",     28, 17, 40, 0),
            new ForecastDay("Tomorrow",  800, "Sunny and Dry", 30, 18, 38, 0),
            new ForecastDay("Tuesday",   801, "Few Clouds",    29, 17, 42, 0),
            new ForecastDay("Wednesday", 802, "Partly Cloudy", 27, 16, 48, 0),
            new ForecastDay("Thursday",  500, "Light Rain",    24, 15, 62, 5),
            new ForecastDay("Friday",    804, "Overcast",      23, 14, 65, 2),
            new ForecastDay("Saturday",  800, "Clear Sky",     27, 16, 45, 0)}));
    }

    // ========================================================
    //  CO4 — CITY LOOKUP  HashMap O(1) + fuzzy fallback O(n)
    // ========================================================

    static City findCity(String input) {
        String key = input.trim().toLowerCase().replaceAll("\\s+", " ");
        if (CITIES.containsKey(key)) return CITIES.get(key);       // O(1)
        for (String k : CITIES.keySet())                           // O(n) fuzzy
            if (k.contains(key) || key.contains(k)) return CITIES.get(k);
        return null;
    }

    // ========================================================
    //  MENU
    // ========================================================

    static void showMenu() {
        System.out.println();
        line('=', 65);
        System.out.println("  SKYCAST MENU");
        line('-', 65);
        System.out.println("  1. Search City Weather");
        System.out.println("  2. Show Recent Searches     (CO2 - Linked List)");
        System.out.println("  3. Show Search History      (CO3 - Stack)");
        System.out.println("  4. Show Weather Alerts      (CO3 - Circular Queue)");
        System.out.println("  5. Show Heat Ranking        (CO3 - Priority Queue)");
        System.out.println("  6. Show Sorting + Search    (CO1)");
        System.out.println("  7. Show HashMap Info        (CO4)");
        System.out.println("  8. Toggle Celsius/Fahrenheit");
        System.out.println("  9. List All Cities");
        System.out.println("  0. Exit");
        line('=', 65);
        System.out.print("  Enter choice: ");
    }

    // ========================================================
    //  MAIN
    // ========================================================

    public static void main(String[] args) {
        loadCities();

        RecentList  recentList  = new RecentList();    // CO2: Linked List
        SearchStack searchStack = new SearchStack();    // CO3a: Stack
        AlertQueue  alertQueue  = new AlertQueue(10);  // CO3b: Circular Queue

        alertQueue.enqueue("Welcome to SkyCast! Search any of the 12 cities.");
        alertQueue.enqueue("Available: Tokyo London NewYork Dubai Sydney Mumbai");
        alertQueue.enqueue("Also: Delhi Bangalore Chennai Kolkata Hyderabad Jaipur");

        Scanner sc   = new Scanner(System.in);
        boolean fah  = false;
        City    last = null;

        System.out.println();
        line('=', 65);
        System.out.println("       SKYCAST  -  Instant Weather Predictor");
        System.out.println("       Java Console  |  Data Structures Demo");
        System.out.println("       CO1  CO2  CO3  CO4  CO5  CO6");
        line('=', 65);

        while (true) {
            showMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    System.out.print("  Enter city name: ");
                    String input = sc.nextLine().trim();
                    if (input.isEmpty()) {
                        System.out.println("  Please enter a city name.");
                        break;
                    }
                    City found = findCity(input);
                    if (found == null) {
                        System.out.println("  City not found.");
                        System.out.println("  Try: Tokyo, London, Mumbai, Delhi, Dubai, Hyderabad...");
                    } else {
                        displayWeather(found, fah);
                        recentList.add(found.name);         // CO2
                        searchStack.push(found.name);       // CO3a
                        generateAlerts(found, alertQueue);  // CO3b
                        last = found;
                    }
                    break;

                case "2":
                    header("RECENT SEARCHES  -  Linked List  (CO2)");
                    System.out.println("  Singly Linked List | insertFront O(1) | max 5");
                    line('-', 65);
                    recentList.print();
                    line('=', 65);
                    break;

                case "3":
                    header("SEARCH HISTORY  -  Stack  (CO3)");
                    System.out.println("  Array Stack | push/pop O(1) | top = last searched");
                    line('-', 65);
                    searchStack.print();
                    line('=', 65);
                    break;

                case "4":
                    header("WEATHER ALERTS  -  Circular Queue  (CO3)");
                    System.out.println("  Fixed-size circular array | enqueue/dequeue O(1)");
                    line('-', 65);
                    alertQueue.printAll();
                    line('=', 65);
                    break;

                case "5":
                    showHeatRanking();
                    break;

                case "6":
                    showSortingDemo();
                    break;

                case "7":
                    showHashMapInfo();
                    break;

                case "8":
                    fah = !fah;
                    System.out.println("  Switched to: " + (fah ? "Fahrenheit (F)" : "Celsius (C)"));
                    if (last != null) displayWeather(last, fah);
                    break;

                case "9":
                    header("ALL AVAILABLE CITIES");
                    List<String> keys = new ArrayList<>(CITIES.keySet());
                    Collections.sort(keys);
                    int n = 1;
                    for (String k : keys)
                        System.out.printf("  %2d. %-15s (%s)%n",
                            n++, CITIES.get(k).name, CITIES.get(k).country);
                    line('=', 65);
                    break;

                case "0":
                    System.out.println();
                    line('=', 65);
                    System.out.println("  Thank you for using SkyCast!");
                    System.out.println("  CO1 CO2 CO3 CO4 CO5 CO6 all demonstrated.");
                    line('=', 65);
                    sc.close();
                    return;

                default:
                    System.out.println("  Invalid choice. Please enter 0 to 9.");
            }
        }
    }
}