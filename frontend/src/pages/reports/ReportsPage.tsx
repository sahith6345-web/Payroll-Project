import React from 'react';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { FileSpreadsheet, FileText, Download, PieChart, Calendar, ShieldCheck } from 'lucide-react';

export const ReportsPage: React.FC = () => {
  const reportsList = [
    {
      title: 'Monthly Salary Register',
      description: 'Comprehensive line-item breakdown of Gross, Allowances (HRA/DA), Deductions (PF/ESI/Tax), and Net Pay across all active employees.',
      icon: FileSpreadsheet,
      type: 'PDF / EXCEL',
    },
    {
      title: 'Attendance & Compliance Summary',
      description: 'Monthly summary of total working hours, late check-ins, early exits, overtime calculations, and leave deductions per department.',
      icon: Calendar,
      type: 'PDF / EXCEL',
    },
    {
      title: 'Statutory Tax & PF Deduction Report',
      description: 'Provident Fund (PF), ESI, Professional Tax, and Income Tax TDS withholding statement for monthly regulatory filing.',
      icon: ShieldCheck,
      type: 'PDF / EXCEL',
    },
    {
      title: 'Departmental Payroll Distribution',
      description: 'Cost center analysis grouping gross payroll allocation and benefits across Engineering, HR, Finance, and Marketing.',
      icon: PieChart,
      type: 'PDF / EXCEL',
    },
  ];

  const handleExport = (reportTitle: string) => {
    alert(`Exporting ${reportTitle}... Download will begin shortly.`);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
          System Reports & Analytics
        </h1>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          Export enterprise payroll registers, compliance documentation, and department analytics.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {reportsList.map((report, idx) => {
          const Icon = report.icon;
          return (
            <Card key={idx} hoverEffect className="flex flex-col justify-between p-6">
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 rounded-xl bg-indigo-600/10 text-indigo-600 dark:text-indigo-400 flex items-center justify-center">
                    <Icon className="w-6 h-6" />
                  </div>
                  <span className="text-xs font-mono font-bold text-slate-400 border border-slate-700 px-2 py-0.5 rounded">
                    {report.type}
                  </span>
                </div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">{report.title}</h3>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-2 leading-relaxed">
                  {report.description}
                </p>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-200 dark:border-slate-800 flex items-center gap-3">
                <Button size="sm" onClick={() => handleExport(report.title)} leftIcon={<Download className="w-4 h-4" />}>
                  Export PDF
                </Button>
                <Button size="sm" variant="outline" onClick={() => handleExport(report.title)} leftIcon={<FileSpreadsheet className="w-4 h-4" />}>
                  Export Excel
                </Button>
              </div>
            </Card>
          );
        })}
      </div>
    </div>
  );
};
