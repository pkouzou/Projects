class CrepeDictionary {

    constructor() {
        this.ingredientDictionary = {}
        this.ingredientFrontNames = {}
        this.price = 0;
        this.addIngredient("ΖύμηΚρέπας", "Ζύμη Κρέπας", 2.20);
        this.isXL = false;
        this.quantity = 1;
    }

    addIngredient(ingredientName, ingredientFrontName, price) {
        ingredientName = "" + ingredientName;
        price = parseFloat(price);
        if (ingredientName in this.ingredientDictionary) {
            if (this.ingredientDictionary[ingredientName] >= 10) {
                return;
            }
            this.ingredientDictionary[ingredientName] += 1;
        }
        else {
            this.ingredientDictionary[ingredientName] = 1;
            this.ingredientFrontNames[ingredientName] = ingredientFrontName
        }

        this.price += price;
    }

    removeIngredient(ingredientName) {
        delete this.ingredientDictionary[ingredientName];
    }

    resetQuantity(ingredientName) {
        this.ingredientDictionary[ingredientName] = -1;
    }

    setValue(ingredientName, value) {
        this.ingredientDictionary[ingredientName] = value;
    }

    changePrice(diff) {
        this.price = parseFloat(this.price).toFixed(2)  - parseFloat(diff).toFixed(2);
    }

    setXL(isXL) {
        this.isXL = isXL;
    }

    changeXL() {
        this.isXL = !this.isXL;
        if (this.isXL) {
            this.price += 1.80;
        }
        else {
            this.price -= 1.80;
        }
    }

    setQuantity(quantity) {
        this.quantity = quantity;
    }

    getingredientDictionary() {
        return this.ingredientDictionary;
    }

    getPrice() {
        return this.price.toFixed(2);
    }

    getTotalPrice() {
       return (parseFloat(this.price).toFixed(2) * this.quantity).toFixed(2);
    }

    getQuantity() {
        return this.quantity;
    }

    priceString() {
        return this.price.toFixed(2) + "";
    }

    totalPriceString() {
        return this.getTotalPrice()+"";
    }

    ingredientsToString() {
       
        var ingredients = Object.values(this.ingredientFrontNames);

        for (var i = 0; i < ingredients.length; i++) {
            var key = ingredients[i].replace(" ", "");
            if (this.ingredientDictionary[key] > 1) {
                ingredients[i] += " x" + this.ingredientDictionary[key];
            }
        }

       return ingredients.join(", ");
    }

    crepeSizeToString() {
        if (this.isXL) return "XL Μέγεθος";
        else return "Κανονικό Μέγεθος";
    }


}