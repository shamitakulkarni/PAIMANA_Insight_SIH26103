from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
app=FastAPI(title="PAIMANA ML Engine")
class Project(BaseModel):
    original_cost:float; revised_cost:float; expenditure:float; physical_progress:float
    original_end_date:str; revised_end_date:str
X=np.array([[2,30,80,0,0],[5,45,70,2,0],[8,55,65,3,0],[12,65,55,5,10],[18,75,45,8,30],[25,90,35,12,55],[30,80,30,15,50],[15,70,50,7,20],[4,40,85,1,0],[22,78,40,10,38],[7,60,72,2,0],[28,95,25,18,70]],float)
y=np.array([0,0,0,0,1,1,1,1,0,1,0,1])
models=[LogisticRegression(max_iter=1000),RandomForestClassifier(n_estimators=150,random_state=42),GradientBoostingClassifier(random_state=42)]
for m in models:m.fit(X,y)
def feat(p):
    infl=((p.revised_cost-p.original_cost)/p.original_cost*100) if p.original_cost else 0
    spend=(p.expenditure/p.original_cost*100) if p.original_cost else 0
    a=list(map(int,p.original_end_date.split("-")));b=list(map(int,p.revised_end_date.split("-")))
    shift=(b[0]-a[0])*12+b[1]-a[1]
    return [infl,spend,p.physical_progress,shift,max(0,spend-p.physical_progress)]
def tier(v):return "HIGH" if v>=.70 else "MEDIUM" if v>=.40 else "LOW"
@app.get("/health")
def health():return {"status":"UP","models":["LogisticRegression","RandomForest","GradientBoosting"]}
@app.post("/predict")
def predict(p:Project):
    f=np.array(feat(p)).reshape(1,-1)
    probs=[float(m.predict_proba(f)[0,1]) for m in models];base=float(np.mean(probs))
    infl,spend,prog,shift,gap=feat(p)
    cp=min(1,max(0,base+max(0,infl-10)/100))
    dp=min(1,max(0,base+max(0,shift-3)/20+max(0,50-prog)/100))
    return {"costRisk":tier(cp),"delayRisk":tier(dp),"overallRisk":tier(max(cp,dp)),"costProbability":cp,"delayProbability":dp,"modelName":"RandomForest + GradientBoosting ensemble","modelVersion":"demo-1.0","benchmark":"LogisticRegression"}
