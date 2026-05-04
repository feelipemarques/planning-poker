Ação              Cliente envia para          Servidor publica em
Entrar            /app/room/{code}/join       /topic/room/{code}/participants
Criar história    /app/room/{code}/create     /topic/room/{code}/stories
Votar             /app/room/{code}/vote       /topic/room/{code}/votes
Revelar           /app/room/{code}/reveal     /topic/room/{code}/votes
Reiniciar         /app/room/{code}/restart    /topic/room/{code}/round