"""64x64 GoodsTrade icon. Two-hand handshake + 3x3 grid."""
from PIL import Image

W = H = 64
BG = (255, 202, 40)
WHITE = (255, 255, 255)
INK = (22, 18, 16)
SKIN = (244, 192, 152)
SKIN_D = (216, 152, 110)
SKIN_S = (184, 118, 82)
NAIL = (252, 222, 198)
SUIT = (32, 34, 42)
SUIT_H = (54, 56, 66)
CUFF = (246, 246, 248)
GOLD = (236, 180, 36)
GOLD_D = (198, 134, 18)
GOLD_L = (250, 224, 110)
WOOD = (128, 84, 44)
WOOD_H = (164, 112, 60)
WOOD_D = (88, 56, 28)
SLOT = (50, 36, 22)
SLOT_H = (70, 50, 30)
RED = (210, 54, 44)
RED_D = (150, 32, 28)
CYAN = (78, 202, 226)
CYAN_D = (36, 144, 180)
CYAN_L = (172, 236, 246)
GREEN = (60, 174, 76)
GREEN_D = (32, 116, 52)
PURP = (162, 78, 192)
PURP_D = (112, 44, 144)
BLUE = (52, 92, 180)
BLUE_D = (32, 60, 132)
TAN = (216, 168, 96)
TAN_D = (170, 118, 58)
PLANK = (170, 122, 66)
PLANK_D = (132, 88, 44)

img = Image.new("RGB", (W, H), BG)
P = img.load()


def put(x, y, c):
    if 0 <= x < W and 0 <= y < H:
        P[x, y] = c


def rect(x, y, w, h, c):
    for yy in range(y, y + h):
        for xx in range(x, x + w):
            put(xx, yy, c)


def hline(a, b, y, c):
    if a > b:
        a, b = b, a
    for x in range(a, b + 1):
        put(x, y, c)


def vline(x, a, b, c):
    if a > b:
        a, b = b, a
    for y in range(a, b + 1):
        put(x, y, c)


def disk(cx, cy, r, c):
    rr = r * r + r
    for y in range(cy - r, cy + r + 1):
        for x in range(cx - r, cx + r + 1):
            if (x - cx) ** 2 + (y - cy) ** 2 <= rr:
                put(x, y, c)


def cap(x0, y0, x1, y1, r, c):
    dx, dy = x1 - x0, y1 - y0
    n = max(abs(dx), abs(dy), 1)
    for i in range(n + 1):
        disk(x0 + round(dx * i / n), y0 + round(dy * i / n), r, c)


def rrect(x, y, w, h, c):
    rect(x + 1, y, w - 2, h, c)
    rect(x, y + 1, w, h - 2, c)


def bar(x, y, w, h, c):
    rrect(x, y, w, h, c)
    disk(x + 2, y + h // 2, max(h // 2, 1), c)
    disk(x + w - 3, y + h // 2, max(h // 2, 1), c)


# =====================================================================
# 3x3 grid (upper right)
# =====================================================================
GX, GY = 36, 3
CELL, DIV, FR = 6, 1, 2
GS = FR * 2 + CELL * 3 + DIV * 4

rect(GX, GY, GS, GS, WOOD_D)
rect(GX + 1, GY + 1, GS - 2, GS - 2, WOOD)
hline(GX + 1, GX + GS - 2, GY + 1, WOOD_H)
vline(GX + 1, GY + 1, GY + GS - 2, WOOD_H)

slots = []
for row in range(3):
    for col in range(3):
        sx = GX + FR + DIV + col * (CELL + DIV)
        sy = GY + FR + DIV + row * (CELL + DIV)
        slots.append((sx, sy))
        rect(sx, sy, CELL, CELL, SLOT)
        hline(sx, sx + CELL - 1, sy, SLOT_H)
        vline(sx, sy, sy + CELL - 1, SLOT_H)


def ingot(x, y):
    hline(x + 1, x + 4, y + 1, GOLD)
    hline(x, x + 5, y + 2, GOLD_L)
    hline(x, x + 5, y + 3, GOLD)
    hline(x + 1, x + 4, y + 4, GOLD_D)


def sword(x, y):
    put(x + 4, y + 0, RED)
    put(x + 5, y + 0, RED)
    put(x + 3, y + 1, RED)
    put(x + 4, y + 1, RED_D)
    put(x + 2, y + 2, RED)
    put(x + 3, y + 2, RED_D)
    put(x + 1, y + 3, TAN)
    put(x + 2, y + 3, RED_D)
    hline(x, x + 2, y + 4, TAN_D)
    put(x + 1, y + 4, TAN)
    put(x + 1, y + 5, TAN_D)


def diamond(x, y):
    put(x + 2, y, CYAN)
    hline(x + 1, x + 4, y + 1, CYAN)
    hline(x, x + 5, y + 2, CYAN_L)
    hline(x + 1, x + 4, y + 3, CYAN)
    put(x + 2, y + 4, CYAN_D)
    put(x + 2, y + 2, WHITE)


def emerald(x, y):
    put(x + 2, y, GREEN)
    hline(x + 1, x + 3, y + 1, GREEN)
    hline(x + 1, x + 4, y + 2, (132, 218, 122))
    hline(x + 1, x + 3, y + 3, GREEN)
    put(x + 2, y + 4, GREEN_D)


def gem(x, y):
    put(x + 2, y, PURP)
    hline(x + 1, x + 4, y + 1, PURP)
    hline(x, x + 5, y + 2, (208, 150, 230))
    hline(x + 1, x + 4, y + 3, PURP)
    put(x + 2, y + 4, PURP_D)


def gblock(x, y):
    rect(x + 1, y + 1, 4, 4, GOLD)
    hline(x + 1, x + 4, y + 1, GOLD_L)
    vline(x + 1, y + 1, y + 4, GOLD_L)
    hline(x + 1, x + 4, y + 4, GOLD_D)
    vline(x + 4, y + 1, y + 4, GOLD_D)


def lapis(x, y):
    hline(x + 1, x + 4, y + 1, BLUE)
    hline(x, x + 5, y + 2, (104, 144, 216))
    hline(x + 1, x + 4, y + 3, BLUE_D)


def plank(x, y):
    rect(x + 1, y + 1, 4, 4, PLANK)
    hline(x + 1, x + 4, y + 2, PLANK_D)
    hline(x + 1, x + 4, y + 4, PLANK_D)
    put(x + 1, y + 1, (192, 144, 82))


for i, fn in enumerate([ingot, sword, diamond, emerald, gem, gblock, lapis, None, plank]):
    if fn:
        fn(*slots[i])


# =====================================================================
# Layer 1: left suit sleeve only (does not extend into the clasp)
# =====================================================================
rrect(2, 18, 14, 22, SUIT)
disk(6, 29, 9, SUIT)
rect(4, 20, 5, 12, SUIT_H)

# Cuff
rrect(13, 20, 7, 18, CUFF)
put(15, 27, GOLD)
put(16, 27, GOLD_L)
put(15, 28, GOLD_D)
put(16, 28, GOLD)

# =====================================================================
# Layer 2: right arm + right hand (bare)
# =====================================================================
cap(36, 32, 49, 48, 7, SKIN)
disk(50, 52, 7, SKIN)
cap(40, 38, 50, 50, 4, SKIN_D)

# right palm
rrect(22, 24, 18, 16, SKIN)
# right thumb — top-right bump, nail left-ish
bar(32, 12, 12, 7, SKIN)
disk(41, 15, 4, SKIN)
rect(32, 13, 2, 5, NAIL)
# right fingers wrapping LEFT, tips stick out past the cuff
bar(8, 24, 20, 5, SKIN)
bar(7, 30, 21, 5, SKIN)
bar(9, 36, 19, 5, SKIN_D)
bar(12, 42, 16, 5, SKIN_D)
rect(8, 25, 2, 3, NAIL)
rect(7, 31, 2, 3, NAIL)
rect(9, 37, 2, 3, NAIL)
rect(12, 43, 2, 3, NAIL)

# =====================================================================
# Layer 3: left hand (over the clasp, fingers wrap right)
# =====================================================================
rrect(17, 22, 16, 15, SKIN)
# left thumb — top-left bump, nail right
bar(18, 10, 13, 7, SKIN)
disk(20, 13, 4, SKIN)
rect(28, 11, 2, 5, NAIL)

bar(26, 19, 20, 5, SKIN)
bar(27, 25, 21, 5, SKIN)
bar(26, 31, 20, 5, SKIN)
bar(24, 37, 18, 5, SKIN_D)
rect(43, 20, 2, 3, NAIL)
rect(45, 26, 2, 3, NAIL)
rect(43, 32, 2, 3, NAIL)
rect(39, 38, 2, 3, NAIL)

# cuff again so it sits between sleeve and left palm
rrect(13, 20, 7, 18, CUFF)
put(15, 27, GOLD)
put(16, 27, GOLD_L)
put(15, 28, GOLD_D)
put(16, 28, GOLD)

# Right-hand finger TIPS must stay visible on the left of the cuff.
# Re-stamp only the left tips so cuff doesn't swallow them.
bar(8, 24, 6, 5, SKIN)
bar(7, 30, 6, 5, SKIN)
bar(9, 36, 6, 5, SKIN_D)
bar(12, 42, 6, 5, SKIN_D)
rect(8, 25, 2, 3, NAIL)
rect(7, 31, 2, 3, NAIL)
rect(9, 37, 2, 3, NAIL)
rect(12, 43, 2, 3, NAIL)

# Finger separators
for y, x0, x1 in (
    (24, 26, 46),
    (30, 27, 47),
    (36, 26, 42),
    (29, 8, 22),
    (35, 9, 24),
    (41, 12, 26),
):
    for x in range(x0, x1):
        if P[x, y] in (SKIN, SKIN_D, NAIL):
            put(x, y, SKIN_S)

# Palm crease
for x, y in ((25, 28), (26, 29), (27, 30), (26, 31), (25, 32), (27, 33)):
    if P[x, y] in (SKIN, SKIN_D):
        put(x, y, SKIN_S)

# Thumb V
for x, y in ((29, 11), (30, 12), (31, 13), (30, 14)):
    if P[x, y] in (SKIN, NAIL, SKIN_D):
        put(x, y, SKIN_S)


# =====================================================================
# 1px ink outline
# =====================================================================
edge = []
for y in range(H):
    for x in range(W):
        if P[x, y] in (BG, INK):
            continue
        for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
            nx, ny = x + dx, y + dy
            if not (0 <= nx < W and 0 <= ny < H) or P[nx, ny] == BG:
                edge.append((x, y))
                break
for x, y in edge:
    put(x, y, INK)

# 2px white sticker
content = [(x, y) for y in range(H) for x in range(W) if P[x, y] != BG]
seen = set()
front = content
for _ in range(2):
    nxt = []
    for x, y in front:
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                if dx == dy == 0:
                    continue
                nx, ny = x + dx, y + dy
                if 0 <= nx < W and 0 <= ny < H and P[nx, ny] == BG and (nx, ny) not in seen:
                    seen.add((nx, ny))
                    nxt.append((nx, ny))
    front = nxt
for x, y in seen:
    put(x, y, WHITE)

path = r"F:\Java\PolangPlugin\GoodsTrade\img\logo-64.png"
img.save(path, "PNG", optimize=True)
print("saved", img.size, "colors", len(set(img.getdata())))
img.resize((256, 256), Image.NEAREST).save(
    r"F:\Java\PolangPlugin\GoodsTrade\img\logo-64-preview.png", "PNG"
)
