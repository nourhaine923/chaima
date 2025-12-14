package com.example.beautymanager;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;
    private final boolean includeEdge;
    private final boolean verticalOnly;
    private final int spanCount;

    /**
     * Constructeur simple - espace uniforme autour des items
     */
    public SpaceItemDecoration(int space) {
        this(space, true, false, 1);
    }

    /**
     * Constructeur avec contrôle sur les bords
     */
    public SpaceItemDecoration(int space, boolean includeEdge) {
        this(space, includeEdge, false, 1);
    }

    /**
     * Constructeur complet pour les grilles
     */
    public SpaceItemDecoration(int space, boolean includeEdge, int spanCount) {
        this(space, includeEdge, false, spanCount);
    }

    /**
     * Constructeur privé principal
     */
    private SpaceItemDecoration(int space, boolean includeEdge, boolean verticalOnly, int spanCount) {
        this.space = space;
        this.includeEdge = includeEdge;
        this.verticalOnly = verticalOnly;
        this.spanCount = Math.max(1, spanCount);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            setupGridOffsets(outRect, position, itemCount);
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            setupStaggeredGridOffsets(outRect, position, itemCount);
        } else {
            setupLinearOffsets(outRect, position, itemCount);
        }
    }

    private void setupGridOffsets(Rect outRect, int position, int itemCount) {
        int column = position % spanCount; // colonne de l'item

        if (includeEdge) {
            outRect.left = space - column * space / spanCount;
            outRect.right = (column + 1) * space / spanCount;

            if (position < spanCount) { // première ligne
                outRect.top = space;
            }
            outRect.bottom = space;
        } else {
            outRect.left = column * space / spanCount;
            outRect.right = space - (column + 1) * space / spanCount;
            if (position >= spanCount) {
                outRect.top = space;
            }
        }
    }

    private void setupStaggeredGridOffsets(Rect outRect, int position, int itemCount) {
        // Pour StaggeredGrid, on utilise une logique similaire à Grid
        setupGridOffsets(outRect, position, itemCount);
    }

    private void setupLinearOffsets(Rect outRect, int position, int itemCount) {
        if (verticalOnly) {
            // Espacement vertical seulement
            if (position == 0) {
                outRect.top = space;
            } else {
                outRect.top = space / 2;
            }

            if (position == itemCount - 1) {
                outRect.bottom = space;
            } else {
                outRect.bottom = space / 2;
            }
        } else {
            // Espacement uniforme
            if (includeEdge) {
                outRect.left = space;
                outRect.right = space;

                if (position == 0) {
                    outRect.top = space;
                } else {
                    outRect.top = space / 2;
                }

                if (position == itemCount - 1) {
                    outRect.bottom = space;
                } else {
                    outRect.bottom = space / 2;
                }
            } else {
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;

                if (position == 0) {
                    outRect.top = space;
                }
            }
        }
    }

    /**
     * Créer un décorateur pour une liste verticale
     */
    public static SpaceItemDecoration createForVerticalList(int space) {
        return new SpaceItemDecoration(space, true, true, 1);
    }

    /**
     * Créer un décorateur pour une grille
     */
    public static SpaceItemDecoration createForGrid(int space, int spanCount) {
        return new SpaceItemDecoration(space, true, spanCount);
    }

    /**
     * Créer un décorateur pour une grille sans bordure
     */
    public static SpaceItemDecoration createForGridNoEdge(int space, int spanCount) {
        return new SpaceItemDecoration(space, false, spanCount);
    }

    /**
     * Vérifie si l'espacement est valide
     */
    public boolean isValid() {
        return space >= 0;
    }

    /**
     * Obtient la taille de l'espacement
     */
    public int getSpace() {
        return space;
    }
}