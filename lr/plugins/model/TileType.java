package model;

/**
 * Тип тайла.
 */
public enum TileType {
    /**
     * Пустой тайл.
     */
    EMPTY,

    /**
     * Тайл с прямым вертикальным участком дороги.
     */
    VERTICAL,

    /**
     * Тайл с прямым горизонтальным участком дороги.
     */
    HORIZONTAL,

    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: справа и снизу от данного тайла.
     */
    LEFT_TOP_CORNER,

    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: слева и снизу от данного тайла.
     */
    RIGHT_TOP_CORNER,

    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: справа и сверху от данного тайла.
     */
    LEFT_BOTTOM_CORNER,

    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: слева и сверху от данного тайла.
     */
    RIGHT_BOTTOM_CORNER,

    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, снизу и сверху от данного тайла.
     */
    LEFT_HEADED_T,

    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: справа, снизу и сверху от данного тайла.
     */
    RIGHT_HEADED_T,

    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, справа и сверху от данного тайла.
     */
    TOP_HEADED_T,

    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, справа и снизу от данного тайла.
     */
    BOTTOM_HEADED_T,

    /**
     * Тайл, выполняющий роль сочленения четырёх других тайлов: со всех сторон от данного тайла.
     */
    CROSSROADS,

    /**
     * Тип тайла пока не известен.
     */
    UNKNOWN
}
