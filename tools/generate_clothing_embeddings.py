import json
import torch
import open_clip

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

colour_ids = list(colours.keys())
colour_texts = list(colours.values())

colour_tokens = tokenizer(colour_texts)

with torch.no_grad():
    colour_embeddings = model.encode_text(
        colour_tokens,
        normalize=True
    )

colour_result = {}

for colour_id, embedding in zip(
    colour_ids,
    colour_embeddings
):
    colour_result[colour_id] = (
        embedding.cpu().tolist()
    )

colour_output_path = (
    "app/src/main/assets/models/"
    "colour_embeddings.json"
)

with open(colour_output_path, "w") as file:
    json.dump(colour_result, file)

print(
    f"Wrote {len(colour_result)} colour embeddings"
)
print(
    f"Colour embedding size: "
    f"{len(next(iter(colour_result.values())))}"
)
print(
    f"Saved to: {colour_output_path}"
)