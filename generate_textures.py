"""
Custom Minecraft hoe skin textures — 16x16 RGBA PNG.
Each skin has a unique silhouette, color scheme, and decorative details.

T = transparent
Color chars are defined per skin in PALETTE dicts.
"""
from PIL import Image
import os

OUT = r"C:\Users\insch\xdGens-fork\resourcepack\assets\xdgens\textures\item"

# ─── FOREST HOE ───────────────────────────────────────────────────────────────
# Wide leaf-shaped blade, wood-grain handle, tiny leaf accents
FOREST_PALETTE = {
    'o': (30,  15,   5, 255),  # dark outline
    'b': (95,  55,  18, 255),  # brown handle mid
    'B': (140, 88,  35, 255),  # brown handle light
    'g': (28,  95,  18, 255),  # dark green
    'G': (55, 150,  28, 255),  # green blade
    'l': (100, 200, 45, 255),  # light green
    'y': (195, 235, 80, 255),  # leaf vein / bright accent
    'v': (65,  40,  10, 255),  # dark bark
}
FOREST = [
    "TTTTTTTTTTTTTTTT",
    "TToloooTTTTTTTTT",
    "TTgGlGgTTTTTTTTT",
    "TTgGlGgTTTTTTTTT",
    "TTTgGoTTTTTTTTTT",
    "TTTTvoTTTTTTTTTT",
    "TTlTTbBTTTTTTTTT",
    "TTTTTvbBTTTTTTTT",
    "TTTTTTvbBTTTTTTT",
    "TTTTTTTvbTTTTTTT",
    "TTTTTlTBbTTTTTTT",
    "TTTTTTTvbBTTTTTT",
    "TTTTTTTTvbTTTTTT",
    "TTTTTTTTTvTTTTTT",
    "TTTTTTTTTTTTTTTT",
    "TTTTTTTTTTTTTTTT",
]

# ─── OCEAN HOE ────────────────────────────────────────────────────────────────
# Trident-like blade with prongs, dark blue handle with lighter stripe
OCEAN_PALETTE = {
    'o': (5,   25,  80, 255),  # dark outline
    'h': (15,  65, 180, 255),  # handle dark blue
    'H': (35, 110, 220, 255),  # handle mid blue
    's': (60, 190, 230, 255),  # blade cyan-blue
    'S': (110, 225, 245, 255), # blade highlight
    'w': (200, 245, 255, 255), # water sparkle
    'p': (25,  80, 150, 255),  # prong dark
    'd': (10,  45, 120, 255),  # deep blue shadow
}
OCEAN = [
    "TTTTTTTTTTTTTTTT",
    "TTosTsTsTTTttTTT",
    "TTsSSSsSSTTTTTTT",
    "TTTsSSoTTTTTTTTT",
    "TTTTsoTTTTTTTTTT",
    "TTTTThHTTTTTTTTT",
    "TTTwTdhHTTTTTTTT",
    "TTTTTTdhHTTTTTTT",
    "TTTTTTTdhHTTTTTT",
    "TTTTTTTTdhHwTTTT",
    "TTTTTTTTTdhHTTTT",
    "TTTTTTTTTTdhHTTT",
    "TTTTTTTTTTTdhHTT",
    "TTTTTTTTTTTTdhTT",
    "TTTTTTTTTTTTTTTT",
    "TTTTTTTTTTTTTTTT",
]

# ─── LAVA HOE ─────────────────────────────────────────────────────────────────
# Dark metal blade with glowing crack lines, ember handle
LAVA_PALETTE = {
    'o': (10,   5,   2, 255),  # near-black outline
    'n': (35,  18,   8, 255),  # dark metal
    'm': (70,  40,  15, 255),  # metal mid
    'M': (100, 60,  25, 255),  # metal light
    'r': (200, 50,   5, 255),  # hot red
    'R': (240, 100,  10, 255), # orange glow
    'y': (255, 200,  20, 255), # yellow-hot core
    'e': (255, 140,   0, 255), # ember orange
}
LAVA = [
    "TTTTTTTTTTTTTTTT",
    "TTonnnnoTTTTTTTT",
    "TTnmRMmnTTTTTTTT",
    "TTnMyRmnoTTTTTTT",
    "TTTnnmnoTTTTTTTT",
    "TTTToneTTTTTTTTT",
    "TTTTTnmMeTTTTTTT",
    "TTTTTonmMeTTTTTT",
    "TTTTTTonmMTTTTTT",
    "TTTTTTTonmeeTTTT",
    "TTTTTTTTonmMTTTT",
    "TTTTTTTTTonmMTTT",
    "TTTTTTTTTTonmeTT",
    "TTTTTTTTTTTonTTT",
    "TTTTTTTTTTTTTTTT",
    "TTTTTTTTTTTTTTTT",
]

# ─── CRYSTAL HOE ──────────────────────────────────────────────────────────────
# Faceted crystal blade with sharp prism geometry, sparkles
CRYSTAL_PALETTE = {
    'o': (40,   5,  70, 255),  # dark outline
    'd': (75,  15, 130, 255),  # deep purple
    'p': (140, 35, 210, 255),  # purple crystal
    'P': (190, 80, 255, 255),  # bright purple
    'k': (220, 130, 255, 255), # pink-purple facet
    'w': (255, 235, 255, 255), # white sparkle
    'c': (160, 60, 240, 255),  # crystal mid
    'v': (60,  10, 100, 255),  # dark vein
}
CRYSTAL = [
    "TTTTwTTTTTTTTTTT",
    "TTodddoTTTwTTTTT",
    "TTdpPkdTTTTTTTTT",
    "TTdPwPdTTTTTTTTT",
    "TTTdpdoTTTTTTTTT",
    "TTTTvdTTTTTTTTTT",
    "TTTTTvdpwTTTTTTT",
    "TTTwTTvdpTTTTTTT",
    "TTTTTTTvdpTTTTTT",
    "TTTTTTTTvdpwTTTT",
    "TTTTTTTTTvdpTTTT",
    "TTTTTTTTTTvdpTTT",
    "TTTTTTTTTTTvdpTT",
    "TTTTTTTTTTTTvdTT",
    "TTTTTTTTTTTTTTTT",
    "TTTTTTTTTTTTTTTT",
]

# ─── SHADOW HOE ───────────────────────────────────────────────────────────────
# Void-black blade with swirling purple energy, nearly invisible
SHADOW_PALETTE = {
    'o': (5,    2,  10, 255),  # void black
    'n': (18,  10,  30, 255),  # near black
    's': (40,  18,  65, 255),  # shadow purple
    'S': (70,  28, 110, 255),  # dark purple
    'e': (120, 35, 190, 255),  # energy purple
    'E': (180, 60, 255, 255),  # bright energy
    'g': (210, 110, 255, 255), # purple glow
    'w': (240, 200, 255, 255), # void light tip
}
SHADOW = [
    "TTTwTTTTTTTTTTTT",
    "TTossSoTTTTTTTTT",
    "TTsSeEsTTTTTTTTT",
    "TTsSEwEsTTTTTTTT",
    "TTTsSsoTTTTTTTTT",
    "TTTTnoTTTTTTTTTT",
    "TTTTTnsSeTTTTTTT",
    "TTETTTnsSeTTTTTT",
    "TTTTTTTnsSeTTTTT",
    "TTTTTTTTnsSgTTTT",
    "TTTTTTTTTnsSeTTT",
    "TTTTTTTTTTnsSeTT",
    "TTTTTTTTTTTnseTT",
    "TTTTTTTTTTTTnsTT",
    "TTTTTTTTTTTTTTTT",
    "TTTTTTTTTTTTTTTT",
]

SKINS = {
    "hoe_forest":  (FOREST,  FOREST_PALETTE),
    "hoe_ocean":   (OCEAN,   OCEAN_PALETTE),
    "hoe_lava":    (LAVA,    LAVA_PALETTE),
    "hoe_crystal": (CRYSTAL, CRYSTAL_PALETTE),
    "hoe_shadow":  (SHADOW,  SHADOW_PALETTE),
}

for name, (grid, palette) in SKINS.items():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for row_idx, row in enumerate(grid):
        for col_idx, ch in enumerate(row[:16]):
            if ch != 'T' and ch != 't' and ch in palette:
                px[col_idx, row_idx] = palette[ch]
    # 2× upscale so pixels are visible in preview
    preview = img.resize((64, 64), Image.NEAREST)
    preview_path = os.path.join(OUT, f"{name}_preview.png")
    preview.save(preview_path)
    path = os.path.join(OUT, f"{name}.png")
    img.save(path)
    print(f"Saved {path}  (+ preview)")

print("Done.")
