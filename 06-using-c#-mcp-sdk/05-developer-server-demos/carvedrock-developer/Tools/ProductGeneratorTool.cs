using System.ComponentModel;
using Bogus;
using Bogus.Extensions;
using ModelContextProtocol.Server;

internal class ProductGeneratorTool
{
    [McpServerTool]
    [Description("Generates a set of CarvedRock products that can be used as test data.")]
    public List<Product> GetTestProducts(
        [Description("Number of products to generate")] int numToGenerate = 10)
    {
        return ProductFaker.Generate(numToGenerate);
    }

    internal static readonly Faker<Product> ProductFaker = new Faker<Product>()
        .RuleFor(p => p.Name, f => f.Commerce.ProductName().ClampLength(max: 50))
        .RuleFor(p => p.Description, f => f.Commerce.ProductDescription().ClampLength(max: 150))
        .RuleFor(p => p.Category, f => f.PickRandom("boots", "equip", "kayak", "clothing"))
        .RuleFor(p => p.Price, (f, p) =>
                p.Category == "boots" ? f.Random.Double(50, 300) :
                p.Category == "equip" ? f.Random.Double(20, 150) :
                p.Category == "clothing" ? f.Random.Double(20, 150) :
                p.Category == "kayak" ? f.Random.Double(100, 500) : 0)
        .RuleFor(p => p.ImgUrl, f => f.Image.PicsumUrl());
}

internal class Product
{
    public int Id { get; set; }
    public string Name { get; set; } = null!;
    public string Description { get; set; } = null!;
    public double Price { get; set; }
    public string Category { get; set; } = null!;
    public string ImgUrl { get; set; } = null!;
}