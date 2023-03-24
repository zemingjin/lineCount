package org.mjtools.linecount;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class LineCount implements Runnable {
    protected static final String MISSING_PATH = "Missing required path to perform line-count.";
    protected static final String DEF_EXT = "java";
    protected static final String VERBOSE = "-verbose";
    protected static final String PATH_SEPARATOR = ":";

    private String path;
    private String fileExt;
    private boolean verbose;
    private final List<Pair<String, Integer>> counts = new ArrayList<>();
    private int lineCount = 0;

    public LineCount(String[] args) {
        path(updatePath(args[0]));
        fileExt(checkArgs(args, "-ext", DEF_EXT));
        verbose(nonNull(checkArgs(args, VERBOSE, null)));
    }

    @Override
    public void run() {
        Stream.of(path().split(PATH_SEPARATOR)).forEach(this::run);
        System.out.printf("Total # of lines: %d\n", lineCount());
        if (verbose()) {
            summaries().forEach(it -> System.out.printf("%s: %d\n", it.getLeft(), it.getRight()));
        }
    }

    private List<Pair<String, Integer>> summaries() {
        return Stream.of(path().split(PATH_SEPARATOR))
                .map(this::getTotal)
                .toList();
    }

    Pair<String, Integer> getTotal(String pathBase) {
        return Pair.of(pathBase,
                counts().stream()
                        .filter(it -> it.getLeft().contains(pathBase))
                        .mapToInt(Pair::getRight)
                        .sum());
    }

    private void run(String path) {
        var file = new File(path);

        if (file.isDirectory()) {
            Optional.ofNullable(file.list())
                    .map(Stream::of)
                    .stream()
                    .flatMap(it -> it)
                    .forEach(it -> run(file.getPath() + File.separator + it));
        } else if (file.getName().endsWith(fileExt)) {
            var result = Pair.of(file.getPath(), countLines(file));
            counts.add(result);
            lineCount(lineCount() + result.getRight());
        }
    }

    protected int countLines(File file) {
        if (verbose()) {
            System.out.printf("Count lines for %s...\n", file.getPath());
        }
        try (var fileReader = new FileReader(file)) {
            return (int) new BufferedReader(fileReader).lines()
                    .filter(it -> !it.isEmpty())
                    .count();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String checkArgs(String[] args, String arg, String def) {
        return Stream.of(args)
                .filter(it -> it.startsWith(arg))
                .map(it -> it.substring(arg.length()))
                .map(it -> it.startsWith("=") ? it.substring(1) : it)
                .findFirst()
                .orElse(def);
    }

    private String updatePath(String path) {
        return path.replace(";", PATH_SEPARATOR).replace("/", File.separator);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new LineCount(args).run();
        } else {
            System.out.println(MISSING_PATH);
        }
    }

    public String path() {
        return path;
    }

    public void path(String path) {
        this.path = path;
    }

    public String fileExt() {
        return fileExt;
    }

    public void fileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public int lineCount() {
        return lineCount;
    }

    public void lineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public boolean verbose() {
        return verbose;
    }

    public void verbose(boolean verbose) {
        this.verbose = verbose;
    }

    public List<Pair<String, Integer>> counts() {
        return counts;
    }
}