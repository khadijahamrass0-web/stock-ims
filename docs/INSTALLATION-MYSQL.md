# Installation MySQL — aide-mémoire

L'application utilise la base `stock_ims` et le compte `stock_app`.
Le mot de passe `root` n'est **pas** nécessaire au fonctionnement de StockIMS.

## 1. Cas normal : accès root disponible

Dans MySQL Workbench, ouvrir la connexion `Local instance MySQL80` puis exécuter :

```sql
SOURCE C:/Users/HP/Desktop/project java/stock-ims/sql/init-db.sql;
SOURCE C:/Users/HP/Desktop/project java/stock-ims/sql/stockims.sql;
```

Ou en ligne de commande, depuis le dossier du projet :

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < sql\init-db.sql
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < sql\stockims.sql
```

Vérification :

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u stock_app "-pStockApp!2026" -e "USE stock_ims; SHOW TABLES; SELECT * FROM article;"
```

## 2. Mot de passe root oublié

### Option A — MySQL Workbench se connecte encore

Si Workbench ouvre la connexion `Local instance MySQL80` sans redemander le mot de
passe, exécuter simplement les deux scripts ci-dessus dans un onglet SQL.
C'est la solution la plus simple et la moins risquée.

### Option B — réinitialiser root avec `--init-file`

À faire dans un **PowerShell administrateur** (clic droit → Exécuter en tant
qu'administrateur). Les données existantes sont conservées.

1. Créer le fichier `C:\ProgramData\MySQL\reset-root.sql` :

```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'VotreNouveauMotDePasse!';
```

2. Arrêter le service, lancer MySQL avec le fichier d'initialisation :

```powershell
net stop MySQL80

& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" `
  --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" `
  --init-file="C:\ProgramData\MySQL\reset-root.sql" `
  --console
```

3. Attendre le message `ready for connections`, puis `Ctrl+C`.

4. Redémarrer le service :

```powershell
net start MySQL80
```

5. Se connecter avec le nouveau mot de passe et exécuter `sql/init-db.sql`
   puis `sql/stockims.sql`.

### Option C — mode `--skip-grant-tables`

Si l'option B échoue :

```powershell
net stop MySQL80
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" `
  --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" `
  --skip-grant-tables --console
```

Dans un **second** PowerShell administrateur :

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root
```

Puis, dans le client MySQL :

```sql
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'VotreNouveauMotDePasse!';
```

Quitter le client (`exit`), revenir au premier PowerShell et faire `Ctrl+C`,
puis :

```powershell
net start MySQL80
```

> Le mode `--skip-grant-tables` ouvre temporairement l'accès sans mot de passe.
> Ne le laisser actif que le temps de la réinitialisation.

## 3. Paramètres à adapter dans le projet

`src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stock_ims?useUnicode=true&characterEncoding=utf8&serverTimezone=Africa/Casablanca
spring.datasource.username=stock_app
spring.datasource.password=StockApp!2026
```

Si vous choisissez un autre mot de passe pour `stock_app`, modifiez à la fois
`sql/init-db.sql` et ce fichier.

## 4. Erreurs fréquentes

| Message | Cause probable | Solution |
|---|---|---|
| `Access denied for user 'stock_app'` | compte absent ou mot de passe différent | exécuter `sql/init-db.sql` avec root |
| `Unknown database 'stock_ims'` | base non créée | exécuter `sql/init-db.sql` |
| `Communications link failure` | service MySQL arrêté | démarrer le service `MySQL80` |
| `Table 'stock_ims.article' doesn't exist` | données non importées | exécuter `sql/stockims.sql` |
| Port 8080 occupé | autre instance Spring Boot | arrêter l'ancienne instance ou changer `server.port` |
