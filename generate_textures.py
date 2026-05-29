"""
Animated scythe-style hoe skin textures — 16x64 RGBA PNG (4 frames × 16px).
Each skin has a unique color scheme and per-frame glow/sparkle animation.
"""
from PIL import Image
import os, json

OUT = r"C:\Users\insch\xdGens-fork\resourcepack\assets\xdgens\textures\item"

# ─── BASE SCYTHE SHAPE ────────────────────────────────────────────────────────
# 0=transparent, 1=blade_dark, 2=blade_mid, 3=blade_light,
# 4=handle_dark, 5=handle_light, 6=glow (animated), 7=sparkle (animated)
BASE = [
    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],  # 0
    [0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0],  # 1  wide blade top
    [0,1,2,2,2,2,2,2,1,0,0,0,0,0,0,0],  # 2  blade body
    [0,1,2,3,3,3,2,1,0,0,0,0,0,0,0,0],  # 3  blade highlight
    [0,1,2,0,0,2,1,0,0,0,0,0,0,0,0,0],  # 4  hollow / inner curve
    [0,1,2,0,2,1,0,0,0,0,0,0,0,0,0,0],  # 5  hollow narrows
    [0,1,1,1,1,0,6,5,0,0,0,0,0,0,0,0],  # 6  blade tip + handle starts
    [0,0,0,0,0,5,4,0,0,0,0,0,0,0,0,0],  # 7  handle
    [0,0,0,0,0,0,5,4,0,0,0,0,0,0,0,0],  # 8
    [0,0,0,0,0,0,0,5,4,0,0,0,0,0,0,0],  # 9
    [0,0,0,0,0,0,0,0,5,4,0,0,0,0,0,0],  # 10
    [0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0],  # 11 handle end
    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],  # 12
    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],  # 13
    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],  # 14
    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],  # 15
]

# Sparkle positions cycling through frames
SPARKLE_FRAMES = [
    [(1, 8), (3, 2)],          # frame 0
    [(7, 1), (2, 5)],          # frame 1
    [(1, 8), (6, 2), (3, 6)],  # frame 2
    [(5, 1), (2, 3)],          # frame 3
]

# ─── SKIN DEFINITIONS ─────────────────────────────────────────────────────────
SKINS = {
    "hoe_forest": {
        1: [(30, 15,  5)],
        2: [(55,150, 28)],
        3: [(120,200, 55)],
        4: [(80, 45, 12)],
        5: [(140, 90, 35)],
        # glow frames: each is an RGBA for code-6 pixel per frame
        "glow": [(60,180,20,200),(90,220,30,255),(130,255,50,255),(70,200,25,220)],
        "sparkle": [(200,255,100,255),(220,255,120,255),(255,255,160,255),(180,240,80,255)],
    },
    "hoe_ocean": {
        1: [(5,  30,100)],
        2: [(20, 90,220)],
        3: [(50,190,240)],
        4: [(15, 55,120)],
        5: [(40,120,190)],
        "glow": [(30,160,230,200),(50,200,255,255),(80,230,255,255),(35,175,240,220)],
        "sparkle": [(180,245,255,255),(200,255,255,255),(230,255,255,255),(160,230,255,255)],
    },
    "hoe_lava": {
        1: [(15,  5,  2)],
        2: [(80, 35, 10)],
        3: [(200, 80, 10)],
        4: [(40, 15,  5)],
        5: [(120, 55, 15)],
        "glow": [(220,90,5,200),(245,130,10,255),(255,180,20,255),(230,100,8,220)],
        "sparkle": [(255,220,30,255),(255,240,60,255),(255,255,100,255),(255,200,20,255)],
    },
    "hoe_crystal": {
        1: [(40,  5, 70)],
        2: [(140, 35,210)],
        3: [(200, 90,255)],
        4: [(65, 12,110)],
        5: [(160, 65,240)],
        "glow": [(180,60,255,200),(210,90,255,255),(240,140,255,255),(190,70,255,220)],
        "sparkle": [(255,220,255,255),(255,240,255,255),(255,255,255,255),(240,200,255,255)],
    },
    "hoe_shadow": {
        1: [(5,   2, 10)],
        2: [(35, 18, 60)],
        3: [(70, 30,110)],
        4: [(18, 10, 28)],
        5: [(55, 25, 90)],
        "glow": [(110,25,190,200),(150,40,230,255),(190,65,255,255),(120,30,200,220)],
        "sparkle": [(200,80,255,255),(220,100,255,255),(240,130,255,255),(180,60,240,255)],
    },
}

def blend(base_color, alpha_factor):
    r, g, b = base_color
    a = int(255 * alpha_factor)
    return (r, g, b, a)

def make_frame(base, palette, frame_idx):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    glow_color    = palette["glow"][frame_idx]
    sparkle_color = palette["sparkle"][frame_idx]
    sparkle_pos   = set(SPARKLE_FRAMES[frame_idx])

    for row in range(16):
        for col in range(16):
            code = base[row][col]
            if code == 0:
                continue
            elif code == 6:
                px[col, row] = glow_color
            elif code == 7:
                px[col, row] = sparkle_color
            else:
                r, g, b = palette[code][0]
                px[col, row] = (r, g, b, 255)

    # Draw sparkles for this frame
    for (col, row) in sparkle_pos:
        if 0 <= row < 16 and 0 <= col < 16:
            existing = px[col, row]
            # Only draw sparkles on non-transparent pixels
            if existing[3] > 0:
                px[col, row] = sparkle_color

    return img

for name, palette in SKINS.items():
    # Stack 4 frames vertically into a 16x64 strip
    strip = Image.new("RGBA", (16, 64), (0, 0, 0, 0))
    for f in range(4):
        frame_img = make_frame(BASE, palette, f)
        strip.paste(frame_img, (0, f * 16))

    tex_path = os.path.join(OUT, f"{name}.png")
    strip.save(tex_path)

    # Write .mcmeta for animation
    mcmeta = {"animation": {"frametime": 4, "interpolate": True}}
    with open(tex_path + ".mcmeta", "w") as fp:
        json.dump(mcmeta, fp, indent=2)

    # Preview: scale up frame 0
    preview = strip.crop((0, 0, 16, 16)).resize((64, 64), Image.NEAREST)
    preview.save(os.path.join(OUT, f"{name}_preview.png"))

    print(f"Saved {name}.png  (16x64, 4 frames, animated)")

print("Done.")
