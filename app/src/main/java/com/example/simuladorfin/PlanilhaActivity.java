package com.example.simuladorfin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class PlanilhaActivity extends AppCompatActivity {
    private TextView tvValor, tvJuros2;
    private ListView listView;
    private double parcela=0,valor,juros, total;
    private int prazo;

    View viewFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planilha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvValor=findViewById(R.id.tvValor);
        tvJuros2=findViewById(R.id.tvJuros2);
        listView=findViewById(R.id.listView);
        valor=getIntent().getDoubleExtra("valor",0);
        juros=getIntent().getDoubleExtra("juros",0);
        prazo=getIntent().getIntExtra("prazo",0);
        tvValor.setText(""+valor);
        tvJuros2.setText(""+juros);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.header_layout,listView,false));
        viewFooter = getLayoutInflater().inflate(R.layout.footer_layout,listView,false);
        listView.addFooterView(viewFooter);
        gerarPlanilhaPrice();
        calcTotJuros();
        //gerando evento para o listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Parcela parcela1=(Parcela)adapterView.getItemAtPosition(i);
//                Toast.makeText(PlanilhaActivity.this,"Valor dos juros: R$ "+
//                        String.format("%.2f",parcela1.getJuros())+
//                        " Valor a deduzir R$ "+
//                        String.format("%.2f",parcela1.getAmort()),Toast.LENGTH_LONG)
//                        .show();
                Snackbar snackbar;
                snackbar=Snackbar.make(view,"Valor dos juros: R$ "+
                        String.format("%.2f",parcela1.getJuros())+
                        " Valor a deduzir R$ "+
                        String.format("%.2f",parcela1.getAmort()),
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_planilha,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.it_sacre) {
            Toast.makeText(this,"Acessou a opção SACRE",Toast.LENGTH_LONG).show();
            gerarPlanilhaSacre();
            calcTotJuros();
        }
        if (item.getItemId()== R.id.it_price) {
            Toast.makeText(this,"Acessou a opção PRICE",Toast.LENGTH_LONG).show();
            gerarPlanilhaPrice();
            calcTotJuros();
        }

        return super.onOptionsItemSelected(item);
    }

    private void gerarPlanilhaPrice() {
        List<Parcela> parcelaList=new ArrayList<>();

        parcela=Price.calcParcela(valor,juros,prazo);

        double jurosParcela, saldoDevedor=valor;

        total=0;

        for(int i=1; i<=prazo; i++){
            jurosParcela=saldoDevedor*juros/100;
            saldoDevedor-=parcela-jurosParcela;
            Parcela p = new Parcela(i,parcela,jurosParcela,parcela-jurosParcela,saldoDevedor);
            parcelaList.add(p);
            total +=jurosParcela;
        }

        ParcelaAdapter parcelaAdapter=new ParcelaAdapter(this,
                R.layout.item_layout, parcelaList);
        listView.setAdapter(parcelaAdapter);
    }

    private void gerarPlanilhaSacre() {
        List<Parcela> parcelaList=new ArrayList<>();
        double amortizacao, saldoDevedor=valor;
        total =0;

        for (int i = 1; i <= prazo; i ++) {
            parcela = Sacre.calcParcela(saldoDevedor, prazo - (i - 1), juros);
            amortizacao = Sacre.calcAmortizacao(saldoDevedor, prazo - (i - 1));

            saldoDevedor -= amortizacao;

            Parcela p = new Parcela(i, parcela, parcela - amortizacao, amortizacao, saldoDevedor);
            parcelaList.add(p);
            total +=parcela-amortizacao;
        }

        ParcelaAdapter parcelaAdapter = new ParcelaAdapter(
                this,
                R.layout.item_layout,
                parcelaList
        );
        listView.setAdapter(parcelaAdapter);
    }

    private void calcTotJuros() {
        TextView tvTotalJuros = viewFooter.findViewById(R.id.tvTotalJuros);
        tvTotalJuros.setText(String.format("%.2f",total));
    }


}