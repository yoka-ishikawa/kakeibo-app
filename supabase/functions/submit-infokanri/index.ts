import { serve } from "https://deno.land/std@0.192.0/http/server.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response(null, {
      status: 204,
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "POST, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type, apikey",
      },
    });
  }

  const { registeredAt, type, category, amount, updateDateTime } =
    await req.json();

  const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
  const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
  const { createClient } = await import(
    "https://esm.sh/@supabase/supabase-js@2"
  );

  const supabase = createClient(supabaseUrl, supabaseKey);

  const { error } = await supabase.from("expenses").insert({
    registeredAt,
    type,
    category,
    amount,
    updateDateTime,
  });

  if (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500,
      headers: { "Access-Control-Allow-Origin": "*" },
    });
  }

  return new Response(JSON.stringify({ message: "登録成功" }), {
    headers: { "Access-Control-Allow-Origin": "*" },
  });
});
