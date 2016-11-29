/*
 * This file is part of the LireRis project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package fr.mgaillard.lire.ris;

import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Reverse image search system using LIRE.
 *
 * @author Mathieu Gaillard
 */
public class LireRis {
    /**
     * Name of the directory which contains the index.
     */
    private static final String INDEX_NAME = "index";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String command = "";
        String path = "";

        if (args.length == 2) {
            command = args[0];
            path = args[1];
        }

        switch(command) {
            case "add_dir":
                AddDirectory(path);
                break;

            case "search":
                SearchImage(path);
                break;

            default:
                System.out.println("Run \"LireRis <command> <path>\"\n" +
                                   "Commands:\n" +
                                   "add_dir: Index images in a directory.\n" +
                                   "search: Search an image into the index.");
                break;
        }
    }

    /**
     * Index the content of a directory.
     * @param directory_path Path to the directory to index.
     */
    public static void AddDirectory(String directory_path) {
        // Checking if it is a directory.
        File f = new File(directory_path);
        if (f.exists() && f.isDirectory()) {
            System.out.println("Indexing images in " + directory_path);
            IndexDirectory(directory_path);
            System.out.println("Finished indexing.");
        } else {
            System.out.println("No directory given as first argument.");
        }
    }

    /**
     * Index the files in a directory.
     * The index is stored in a directory called INDEX_NAME.
     * @param directory_path Path to the directory to index.
     */
    public static void IndexDirectory(String directory_path) {
        // use ParallelIndexer to index all photos from directory into INDEX_NAME.
        ParallelIndexer indexer = new ParallelIndexer(6, INDEX_NAME, directory_path);
        // Use the CEDD builder.
        indexer.addExtractor(CEDD.class);
        indexer.run();
    }

    /**
     * Search an image into the index.
     * The index is stored in a directory called INDEX_NAME.
     * @param image_path Path to the image.
     */
    public static void SearchImage(String image_path) {
        // Checking if image_path is there and if it is an image.
        File f = new File(image_path);
        if (f.exists()) {
            try {
                BufferedImage image = ImageIO.read(f);
                IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_NAME)));
                ImageSearcher searcher = new GenericFastImageSearcher(30, CEDD.class);

                // Searching with a image file.
                ImageSearchHits hits = searcher.search(image, ir);
                for (int i = 0; i < hits.length(); i++) {
                    String fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
                    System.out.println(hits.score(i) + ": \t" + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}