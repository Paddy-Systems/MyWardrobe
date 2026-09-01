import json
import torch
import open_clip

MODEL_NAME = "hf-hub:Marqo/marqo-fashionSigLIP"

clothing_types = {
    # Tops
    "tshirt": "t-shirt",
    "long_sleeve_tshirt": "long-sleeve t-shirt",
    "graphic_tshirt": "graphic t-shirt",
    "polo_shirt": "polo shirt",
    "shirt": "shirt",
    "dress_shirt": "dress shirt",
    "overshirt": "overshirt",
    "blouse": "blouse",
    "tank_top": "tank top",
    "vest_top": "vest top",
    "camisole": "camisole",
    "crop_top": "crop top",
    "tube_top": "tube top",
    "halter_top": "halter top",
    "bodysuit": "bodysuit",
    "sports_top": "sports top",
    "jersey": "sports jersey",

    # Knitwear / sweatwear
    "jumper": "jumper",
    "sweater": "sweater",
    "hoodie": "hoodie",
    "zip_hoodie": "zip-up hoodie",
    "sweatshirt": "sweatshirt",
    "cardigan": "cardigan",
    "turtleneck": "turtleneck",
    "knitted_vest": "knitted vest",
    "fleece": "fleece",

    # Jackets / outerwear
    "jacket": "jacket",
    "denim_jacket": "denim jacket",
    "leather_jacket": "leather jacket",
    "bomber_jacket": "bomber jacket",
    "biker_jacket": "biker jacket",
    "varsity_jacket": "varsity jacket",
    "track_jacket": "track jacket",
    "windbreaker": "windbreaker",
    "rain_jacket": "rain jacket",
    "puffer_jacket": "puffer jacket",
    "blazer": "blazer",
    "waistcoat": "waistcoat",
    "gilet": "gilet",
    "coat": "coat",
    "overcoat": "overcoat",
    "trench_coat": "trench coat",
    "parka": "parka",
    "pea_coat": "pea coat",
    "duffle_coat": "duffle coat",
    "raincoat": "raincoat",

    # Trousers / bottoms
    "jeans": "jeans",
    "skinny_jeans": "skinny jeans",
    "straight_jeans": "straight-leg jeans",
    "wide_leg_jeans": "wide-leg jeans",
    "flared_jeans": "flared jeans",
    "trousers": "trousers",
    "chinos": "chinos",
    "cargo_trousers": "cargo trousers",
    "dress_trousers": "dress trousers",
    "wide_leg_trousers": "wide-leg trousers",
    "leggings": "leggings",
    "jeggings": "jeggings",
    "joggers": "joggers",
    "sweatpants": "sweatpants",
    "track_pants": "track pants",

    # Shorts
    "shorts": "shorts",
    "denim_shorts": "denim shorts",
    "cargo_shorts": "cargo shorts",
    "chino_shorts": "chino shorts",
    "sports_shorts": "sports shorts",
    "cycling_shorts": "cycling shorts",
    "board_shorts": "board shorts",

    # Skirts
    "skirt": "skirt",
    "mini_skirt": "mini skirt",
    "midi_skirt": "midi skirt",
    "maxi_skirt": "maxi skirt",
    "denim_skirt": "denim skirt",
    "pencil_skirt": "pencil skirt",
    "pleated_skirt": "pleated skirt",
    "a_line_skirt": "A-line skirt",
    "wrap_skirt": "wrap skirt",

    # Dresses
    "dress": "dress",
    "mini_dress": "mini dress",
    "midi_dress": "midi dress",
    "maxi_dress": "maxi dress",
    "bodycon_dress": "bodycon dress",
    "shift_dress": "shift dress",
    "shirt_dress": "shirt dress",
    "wrap_dress": "wrap dress",
    "slip_dress": "slip dress",
    "summer_dress": "summer dress",
    "evening_dress": "evening dress",
    "cocktail_dress": "cocktail dress",
    "formal_dress": "formal dress",
    "gown": "gown",

    # One-piece garments
    "playsuit": "playsuit",
    "romper": "romper",
    "jumpsuit": "jumpsuit",
    "dungarees": "dungarees",
    "overalls": "overalls",
    "boilersuit": "boilersuit",

    # Activewear
    "tracksuit": "tracksuit",
    "gym_top": "gym top",
    "gym_leggings": "gym leggings",
    "gym_shorts": "gym shorts",
    "sports_bra": "sports bra",
    "running_jacket": "running jacket",
    "cycling_jersey": "cycling jersey",

    # Nightwear / loungewear
    "pyjamas": "pyjamas",
    "pyjama_top": "pyjama top",
    "pyjama_bottoms": "pyjama bottoms",
    "nightdress": "nightdress",
    "dressing_gown": "dressing gown",
    "robe": "robe",
    "onesie": "onesie",
    "loungewear": "loungewear",

    # Underwear
    "bra": "bra",
    "bralette": "bralette",
    "underwear": "underwear",
    "boxers": "boxers",
    "briefs": "briefs",
    "knickers": "knickers",
    "thermal_top": "thermal top",
    "thermal_bottoms": "thermal bottoms",

    # Swimwear
    "swimsuit": "swimsuit",
    "bikini": "bikini",
    "bikini_top": "bikini top",
    "bikini_bottom": "bikini bottom",
    "swim_shorts": "swim shorts",
    "swim_trunks": "swim trunks",
    "rash_vest": "rash vest",

    # Footwear
    "shoes": "shoes",
    "trainers": "trainers",
    "sneakers": "sneakers",
    "boots": "boots",
    "ankle_boots": "ankle boots",
    "chelsea_boots": "Chelsea boots",
    "combat_boots": "combat boots",
    "walking_boots": "walking boots",
    "wellies": "Wellington boots",
    "heels": "heels",
    "high_heels": "high heels",
    "court_shoes": "court shoes",
    "flats": "flat shoes",
    "ballet_flats": "ballet flats",
    "loafers": "loafers",
    "brogues": "brogues",
    "oxford_shoes": "Oxford shoes",
    "sandals": "sandals",
    "sliders": "sliders",
    "flip_flops": "flip-flops",
    "espadrilles": "espadrilles",
    "slippers": "slippers",
    "work_shoes": "work shoes",

    # Formalwear
    "suit": "suit",
    "suit_jacket": "suit jacket",
    "tuxedo": "tuxedo",
    "morning_suit": "morning suit",

    # Accessories / wearable items
    "hat": "hat",
    "cap": "cap",
    "beanie": "beanie",
    "bucket_hat": "bucket hat",
    "scarf": "scarf",
    "gloves": "gloves",
    "belt": "belt",
    "tie": "tie",
    "bow_tie": "bow tie",
    "socks": "socks",
    "tights": "tights",
    "stockings": "stockings",
}

print("Loading FashionSigLIP...")

model, _, _ = open_clip.create_model_and_transforms(
    MODEL_NAME
)

tokenizer = open_clip.get_tokenizer(
    MODEL_NAME
)

model.eval()

ids = list(clothing_types.keys())
texts = list(clothing_types.values())

tokens = tokenizer(texts)

with torch.no_grad():
    embeddings = model.encode_text(
        tokens,
        normalize=True
    )

result = {}

for clothing_id, embedding in zip(ids, embeddings):
    result[clothing_id] = embedding.cpu().tolist()

output_path = "app/src/main/assets/models/clothing_embeddings.json"

with open(output_path, "w") as file:
    json.dump(result, file)

print(f"Wrote {len(result)} clothing embeddings")
print(f"Embedding size: {len(next(iter(result.values())))}")
print(f"Saved to: {output_path}")