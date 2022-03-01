package Archiver;

public class FileProperties {

    private String name;
    private long size;
    private long compressedSize;
    private int compressionMethod;

    public FileProperties(String name, long size, long compressedSize, int compressionMethod) {
        this.name = name;
        this.size = size;
        this.compressedSize = compressedSize;
        this.compressionMethod = compressionMethod;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public long getCompressionRatio(){
        // Вычисляем степень сжатия
        return 100 - ((compressedSize * 100) / size);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        if(size>0){
            stringBuilder.append("\t");
            stringBuilder.append(size/1024);
            stringBuilder.append(" Kb (");
            stringBuilder.append(compressedSize/1024);
            stringBuilder.append(" Kb) сжатие:");
            stringBuilder.append(getCompressionRatio());
            stringBuilder.append("%");
        }
            return stringBuilder.toString();
    }
}
