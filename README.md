LireRis
=======
LireRis is a reverse image search engine based on LIRE (Lucene Image Retrieval). This software is developed as part of a specific student project at INSA Lyon, France.  

Features
--------

- Index the images from a directory
- Search for an image even with a slightly modified version

How it works ?
--------------
LireRis makes use of LIRE in order to extract the CEDD features of the images. A distance is then computed between the query image and the indexed images. The most relevant image is the nearest to the query image.

Installation
----------------

You first need to clone the project on your computer, and build it with the following command.

```bash
# Build the project with Gradle
$ gradle build
```
The jar archive is then located in the `build/libs` directory.

Usage
-----

### 
```bash
# Index the content of a directory
$ java -jar lire-ris-x.y.z.jar add_dir <dir_path>

# Search for an image
$ java -jar lire-ris-x.y.z.jar search <file_path>
```

License
-------
See the LICENSE file.