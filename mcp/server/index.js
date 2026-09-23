import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";

const baseUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

async function request(path, options = {}) {
  const headers = { "Content-Type": "application/json" };
  if (process.env.MCP_JWT_TOKEN) headers.Authorization = `Bearer ${process.env.MCP_JWT_TOKEN}`;
  const response = await fetch(`${baseUrl}${path}`, { ...options, headers: { ...headers, ...options.headers } });
  const body = await response.text();
  if (!response.ok) throw new Error(`Backend ${response.status}: ${body}`);
  return body ? JSON.parse(body) : {};
}

const server = new McpServer({ name: "stock-api", version: "0.1.0" });

server.tool("get_health", "Read the stock API health status", {}, async () => ({
  content: [{ type: "text", text: JSON.stringify(await request("/actuator/health")) }]
}));

server.tool("list_categories", "List stock categories", {}, async () => ({
  content: [{ type: "text", text: JSON.stringify(await request("/api/v1/categories")) }]
}));

server.tool("create_category", "Create a stock category", {
  name: z.string().min(1), description: z.string().optional()
}, async ({ name, description }) => ({
  content: [{ type: "text", text: JSON.stringify(await request("/api/v1/categories", {
    method: "POST", body: JSON.stringify({ name, description })
  })) }]
}));

server.tool("list_products", "List stock products", {}, async () => ({
  content: [{ type: "text", text: JSON.stringify(await request("/api/v1/products")) }]
}));

server.tool("create_product", "Create a stock product", {
  sku: z.string().min(3), name: z.string().min(1), categoryId: z.string().uuid(),
  price: z.number().positive(), quantity: z.number().int().nonnegative(), alertThreshold: z.number().int().nonnegative()
}, async (product) => ({
  content: [{ type: "text", text: JSON.stringify(await request("/api/v1/products", {
    method: "POST", body: JSON.stringify(product)
  })) }]
}));

await server.connect(new StdioServerTransport());