import json
import torch
import open_clip
from pathlib import Path

MODEL_NAME = "hf-hub:Marqo/marqo-fashionSigLIP"

clothing_types = {
    # Tops
    "tshirt": "a t-shirt",
    "shirt": "a shirt",
    "blouse": "a blouse",
    "vest_top": "a vest top",
    "bodysuit": "a bodysuit",

    # Knitwear
    "jumper": "a jumper",
    "hoodie": "a hoodie",
    "cardigan": "a cardigan",

    # Outerwear
    "jacket": "a jacket",
    "blazer": "a blazer",
    "coat": "a coat",

    # Bottoms
    "jeans": "a pair of jeans",
    "trousers": "a pair of trousers",
    "leggings": "a pair of leggings",
    "joggers": "a pair of joggers",
    "shorts": "a pair of shorts",
    "skirt": "a skirt",

    # One-piece
    "dress": "a dress",
    "jumpsuit": "a jumpsuit",
    "playsuit": "a playsuit",

    # Other clothing
    "tracksuit": "a tracksuit",
    "pyjamas": "pyjamas",
    "swimsuit": "a swimsuit",

    # Footwear
    "trainers": "a pair of trainers",
    "shoes": "a pair of shoes",
    "boots": "a pair of boots",
    "sandals": "a pair of sandals",
    "heels": "a pair of high heels",
}

colours = {
    "black": "a black item of clothing",
    "white": "a white item of clothing",
    "grey": "a grey item of clothing",
    "blue": "a blue item of clothing",
    "navy": "a navy blue item of clothing",
    "red": "a red item of clothing",
    "green": "a green item of clothing",
    "yellow": "a yellow item of clothing",
    "orange": "an orange item of clothing",
    "pink": "a pink item of clothing",
    "purple": "a purple item of clothing",
    "brown": "a brown item of clothing",
    "beige": "a beige item of clothing",
    "cream": "a cream item of clothing",
    "teal": "a teal item of clothing",
}

semantic_vocabularies = {
    "patterns": {
        "solid": "plain solid colour clothing with no pattern",
        "striped": "striped patterned clothing",
        "checked": "checked pattern clothing",
        "plaid": "plaid tartan clothing",
        "floral": "floral print clothing",
        "graphic": "graphic print clothing with an image or logo",
        "polka_dot": "polka dot clothing",
        "animal_print": "animal print clothing",
        "camouflage": "camouflage pattern clothing",
        "abstract": "abstract patterned clothing",
    },

    "materials": {
        "denim": "denim clothing fabric",
        "cotton": "cotton clothing fabric",
        "wool": "wool clothing fabric",
        "leather": "leather clothing",
        "suede": "suede clothing",
        "knit": "knitted clothing fabric",
        "linen": "linen clothing fabric",
        "satin": "satin clothing fabric",
        "silk": "silk clothing fabric",
        "fleece": "fleece clothing fabric",
        "velvet": "velvet clothing fabric",
    },

    "styles": {
        "casual": "casual everyday clothing",
        "smart": "smart well-dressed clothing",
        "formal": "formal elegant clothing",
        "sporty": "sporty athletic clothing",
        "streetwear": "streetwear fashion clothing",
        "vintage": "vintage retro fashion clothing",
        "minimalist": "minimalist simple fashion clothing",
        "party": "party going-out clothing",
        "workwear": "professional workwear clothing",
        "outdoors": "outdoor practical clothing",
    },

    "occasions": {
        "everyday": "clothing for everyday wear",
        "work": "clothing for work or the office",
        "evening": "clothing for an evening out",
        "party": "clothing for a party",
        "wedding": "clothing to wear to a wedding",
        "gym": "clothing for the gym",
        "sports": "clothing for playing sports",
        "holiday": "clothing for a holiday or vacation",
        "beach": "clothing for the beach",
        "outdoors": "clothing for outdoor activities",
    },

    "seasons": {
        "spring": "clothing suitable for spring weather",
        "summer": "clothing suitable for hot summer weather",
        "autumn": "clothing suitable for autumn or fall weather",
        "winter": "clothing suitable for cold winter weather",
    },

    "formalities": {
        "casual": "casual informal clothing",
        "smart_casual": "smart casual clothing",
        "formal": "formal dressy clothing",
    },
}

print("Loading FashionSigLIP...")
OUTPUT_DIRECTORY = Path(
    "app/src/main/assets/models"
)

OUTPUT_DIRECTORY.mkdir(
    parents=True,
    exist_ok=True
)

print("Loading FashionSigLIP...")

model, _, _ = open_clip.create_model_and_transforms(
    MODEL_NAME
)

tokenizer = open_clip.get_tokenizer(
    MODEL_NAME
)

model.eval()


def encode_prompts(prompts):
    ids = list(prompts.keys())
    texts = list(prompts.values())

    tokens = tokenizer(texts)

    with torch.no_grad():
        embeddings = model.encode_text(
            tokens,
            normalize=True
        )

    return {
        item_id: embedding.cpu().tolist()
        for item_id, embedding
        in zip(ids, embeddings)
    }


def write_embeddings(
    filename,
    embeddings
):
    output_path = (
        OUTPUT_DIRECTORY /
        filename
    )

    with output_path.open(
        "w",
        encoding="utf-8"
    ) as file:
        json.dump(
            embeddings,
            file
        )

    print(
        f"Saved {filename}"
    )


clothing_embeddings = encode_prompts(
    clothing_types
)

write_embeddings(
    "clothing_embeddings.json",
    clothing_embeddings
)


colour_embeddings = encode_prompts(
    colours
)

write_embeddings(
    "colour_embeddings.json",
    colour_embeddings
)


semantic_embeddings = {}

for category, prompts in (
    semantic_vocabularies.items()
):
    print(
        f"Generating {category}..."
    )

    semantic_embeddings[category] = (
        encode_prompts(prompts)
    )


write_embeddings(
    "semantic_embeddings.json",
    semantic_embeddings
)

print("Done.")