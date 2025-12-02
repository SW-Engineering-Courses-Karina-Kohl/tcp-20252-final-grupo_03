import java.io.*;
import java.util.*;

public class RankingManager {
    public static final String RANKING_FILE = "ranking.csv";

    public static class ScoreEntry {
        public final String name;
        public final int score;
        public final long timeMs;

        public ScoreEntry(String name, int score, long timeMs) {
            this.name = name;
            this.score = score;
            this.timeMs = timeMs;
        }

        public String toCSV() {
            return escape(name) + "," + score + "," + timeMs;
        }

        public static ScoreEntry fromCSV(String line) {
            String[] parts = splitCSV(line);
            if (parts.length < 3) return null;
            try {
                String name = unescape(parts[0]);
                int score = Integer.parseInt(parts[1]);
                long time = Long.parseLong(parts[2]);
                return new ScoreEntry(name, score, time);
            } catch (Exception e) {
                return null;
            }
        }

        private static String escape(String s) {
            return s.replace("\\", "\\\\").replace(",", "\\,");
        }

        private static String unescape(String s) {
            return s.replace("\\,", ",").replace("\\\\", "\\");
        }

        private static String[] splitCSV(String line) {
            List<String> parts = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean esc = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (esc) { cur.append(c); esc = false; }
                else if (c == '\\') { esc = true; }
                else if (c == ',') { parts.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
            parts.add(cur.toString());
            return parts.toArray(new String[0]);
        }
    }

    public static List<ScoreEntry> loadScores() {
        List<ScoreEntry> out = new ArrayList<>();
        File f = new File(RANKING_FILE);
        if (!f.exists()) return out;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                ScoreEntry e = ScoreEntry.fromCSV(line);
                if (e != null) out.add(e);
            }
        } catch (IOException ex) {
            // ignore
        }
        return out;
    }

    public static void saveScores(List<ScoreEntry> list) {
        File f = new File(RANKING_FILE);
        try (PrintWriter w = new PrintWriter(new FileWriter(f))) {
            for (ScoreEntry e : list) {
                w.println(e.toCSV());
            }
        } catch (IOException ex) {
            // ignore
        }
    }

    public static void addScore(String name, int score, long timeMs) {
        List<ScoreEntry> list = loadScores();
        list.add(new ScoreEntry(name, score, timeMs));
        // sort: score desc, time asc
        list.sort((a,b) -> {
            if (b.score != a.score) return Integer.compare(b.score, a.score);
            return Long.compare(a.timeMs, b.timeMs);
        });
        saveScores(list);
    }

    public static List<ScoreEntry> getTop(int n) {
        List<ScoreEntry> list = loadScores();
        list.sort((a,b) -> {
            if (b.score != a.score) return Integer.compare(b.score, a.score);
            return Long.compare(a.timeMs, b.timeMs);
        });
        if (list.size() > n) return list.subList(0, n);
        return list;
    }
}
