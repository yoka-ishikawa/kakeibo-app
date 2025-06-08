import { serve } from "https://deno.land/std@0.192.0/http/server.ts";

serve(async (req) => {
  const authHeader = req.headers.get("Authorization");
  // 認証ヘッダーが存在し、Bearerトークンが正しいか確認
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return new Response(JSON.stringify({ error: "Unauthorized" }), {
      status: 401,
      headers: { "Access-Control-Allow-Origin": "*" },
    });
  }

  const token = authHeader.split(" ")[1];
  // トークンの正当性の検証処理
  if (!token || token !== Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")) {
    return new Response(JSON.stringify({ error: "Invalid token" }), {
      status: 401,
      headers: { "Access-Control-Allow-Origin": "*" },
    });
  }

  // クライアントから送信された収支データを取得
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
