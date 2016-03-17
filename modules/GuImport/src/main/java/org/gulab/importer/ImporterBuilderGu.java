

package org.gulab.importer;

import org.gephi.io.importer.api.FileType;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.io.importer.spi.FileImporterBuilder;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = FileImporterBuilder.class)
public final class ImporterBuilderGu implements FileImporterBuilder {

    public static final String IDENTIFER = "gugraph";

    @Override
    public FileImporter buildImporter() {
        return new ImporterGu();
    }

    @Override
    public String getName() {
        return IDENTIFER;
    }

    @Override
    public FileType[] getFileTypes() {
        FileType ft1 = new FileType(".txt", "Gulab graph file type");
        return new FileType[]{ft1, ft2};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        return fileObject.getExt().equalsIgnoreCase("txt");
    }
}
